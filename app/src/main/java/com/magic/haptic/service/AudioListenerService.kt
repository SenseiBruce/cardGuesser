package com.magic.haptic.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.magic.haptic.card.CardRepository
import com.magic.haptic.data.AppDataStore
import com.magic.haptic.data.HapticConfig
import com.magic.haptic.data.ServiceEventBus
import com.magic.haptic.data.ServiceStatus
import com.magic.haptic.data.SpeechLogEntry
import com.magic.haptic.haptic.HapticEncoder
import com.magic.haptic.haptic.HapticPlayer
import com.magic.haptic.parser.NumberWordConverter
import com.magic.haptic.parser.TriggerParser
import com.magic.haptic.speech.SpeechJsonExtractor
import com.magic.haptic.speech.VoskRecognizerManager
import com.magic.haptic.util.AppLogger
import com.magic.haptic.util.CrashReporter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class AudioListenerService : Service() {
    private lateinit var voskManager: VoskRecognizerManager
    private lateinit var triggerParser: TriggerParser
    private lateinit var cardRepository: CardRepository
    private lateinit var hapticEncoder: HapticEncoder
    private lateinit var hapticPlayer: HapticPlayer
    private lateinit var dataStore: AppDataStore
    private lateinit var notificationHelper: NotificationHelper
    private lateinit var speechProcessor: SpeechProcessor

    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private var currentHapticConfig = HapticConfig(100, 300, 150, 500) // Normal default

    override fun onCreate() {
        super.onCreate()

        dataStore = AppDataStore(this)
        notificationHelper = NotificationHelper(this)
        voskManager = VoskRecognizerManager(this)
        triggerParser = TriggerParser(NumberWordConverter())
        cardRepository = CardRepository(dataStore)
        hapticEncoder = HapticEncoder()
        hapticPlayer = HapticPlayer(this)
        speechProcessor = SpeechProcessor(triggerParser, cardRepository, hapticEncoder)

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            startForeground(1, notificationHelper.buildNotification(), android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_MICROPHONE)
        } else {
            startForeground(1, notificationHelper.buildNotification())
        }
        observeSettings()
    }

    override fun onStartCommand(
        intent: Intent?,
        flags: Int,
        startId: Int,
    ): Int {
        ServiceEventBus.startSession()
        ServiceEventBus.updateStatus(ServiceStatus.INITIALIZING)

        voskManager.initModel { success ->
            if (success) {
                ServiceEventBus.updateStatus(ServiceStatus.LISTENING)
                startListening()
            } else {
                ServiceEventBus.updateStatus(ServiceStatus.ERROR)
            }
        }

        return START_STICKY
    }

    private fun startListening() {
        voskManager.startListening(
            object : VoskRecognizerManager.RecognitionCallback {
                override fun onPartialResult(text: String) {
                    // Ignore empty partials
                    if (text.isEmpty() || text == "{\"partial\" : \"\"}") return

                    serviceScope.launch {
                        val partialText = SpeechJsonExtractor.extract(text, "partial")
                        ServiceEventBus.emitSpeechLog(SpeechLogEntry(partialText))
                        processSpeech(partialText)
                    }
                }

                override fun onResult(text: String) {
                    // Ignore empty results
                    if (text.isEmpty() || text == "{\"text\" : \"\"}") return

                    serviceScope.launch {
                        val resultText = SpeechJsonExtractor.extract(text, "text")
                        ServiceEventBus.emitSpeechLog(SpeechLogEntry(resultText))
                        processSpeech(resultText)
                    }
                }

                override fun onError(e: Exception) {
                    AppLogger.e(
                        "speech_recognition_error",
                        e,
                        fields =
                            mapOf(
                                "event" to "speech_error",
                                "errorType" to (e::class.simpleName ?: "Exception"),
                            ),
                    )
                    CrashReporter.record(e, mapOf("component" to "AudioListenerService"))
                    ServiceEventBus.updateStatus(ServiceStatus.ERROR)
                    // Retry logic after 1s
                    serviceScope.launch {
                        delay(1000)
                        startListening()
                    }
                }
            },
        )
    }

    private suspend fun processSpeech(text: String) {
        val result = speechProcessor.process(text, currentHapticConfig) ?: return
        ServiceEventBus.emitTrigger(result.trigger)
        hapticPlayer.vibrate(result.pattern)
    }

    private fun observeSettings() {
        serviceScope.launch {
            dataStore.debounceSec.collectLatest { sec ->
                triggerParser.setDebounce(sec)
            }
        }

        serviceScope.launch {
            combine(
                dataStore.speedPreset,
                dataStore.customShort,
                dataStore.customLong,
                dataStore.customGap,
                dataStore.customSep,
            ) { preset, s, l, g, sep ->
                when (preset) {
                    "FAST" -> HapticConfig(80, 200, 100, 350)
                    "SLOW" -> HapticConfig(150, 400, 200, 600)
                    "CUSTOM" -> HapticConfig(s, l, g, sep)
                    else -> HapticConfig(100, 300, 150, 500)
                }
            }.collectLatest { config ->
                currentHapticConfig = config
            }
        }

        serviceScope.launch {
            combine(dataStore.notifTitle, dataStore.notifBody) { title, body ->
                notificationHelper.buildNotification(title, body)
            }.collectLatest { notification ->
                // Update notification in realtime
                val notificationManager = getSystemService(NOTIFICATION_SERVICE) as android.app.NotificationManager
                notificationManager.notify(1, notification)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        voskManager.release()
        serviceScope.cancel()
        ServiceEventBus.stopSession()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    // Simple helper function to combine flows
    private fun <T1, T2, T3, T4, T5, R> combine(
        flow1: kotlinx.coroutines.flow.Flow<T1>,
        flow2: kotlinx.coroutines.flow.Flow<T2>,
        flow3: kotlinx.coroutines.flow.Flow<T3>,
        flow4: kotlinx.coroutines.flow.Flow<T4>,
        flow5: kotlinx.coroutines.flow.Flow<T5>,
        transform: suspend (T1, T2, T3, T4, T5) -> R,
    ): kotlinx.coroutines.flow.Flow<R> {
        return kotlinx.coroutines.flow.combine(flow1, flow2, flow3, flow4, flow5, transform)
    }

    private fun <T1, T2, R> combine(
        flow1: kotlinx.coroutines.flow.Flow<T1>,
        flow2: kotlinx.coroutines.flow.Flow<T2>,
        transform: suspend (T1, T2) -> R,
    ): kotlinx.coroutines.flow.Flow<R> {
        return kotlinx.coroutines.flow.combine(flow1, flow2, transform)
    }
}
