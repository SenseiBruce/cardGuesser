package com.magic.haptic.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.magic.haptic.card.CardRepository
import com.magic.haptic.data.*
import com.magic.haptic.haptic.HapticEncoder
import com.magic.haptic.haptic.HapticPlayer
import com.magic.haptic.parser.NumberWordConverter
import com.magic.haptic.parser.TriggerParser
import com.magic.haptic.speech.VoskRecognizerManager
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collectLatest
import org.json.JSONObject

class AudioListenerService : Service() {

    private lateinit var voskManager: VoskRecognizerManager
    private lateinit var triggerParser: TriggerParser
    private lateinit var cardRepository: CardRepository
    private lateinit var hapticEncoder: HapticEncoder
    private lateinit var hapticPlayer: HapticPlayer
    private lateinit var dataStore: AppDataStore
    private lateinit var notificationHelper: NotificationHelper

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

        startForeground(1, notificationHelper.buildNotification())
        observeSettings()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
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
        voskManager.startListening(object : VoskRecognizerManager.RecognitionCallback {
            override fun onPartialResult(text: String) {
                // Ignore empty partials
                if (text.isEmpty() || text == "{\"partial\" : \"\"}") return
                
                serviceScope.launch {
                    val partialText = extractJsonText(text, "partial")
                    ServiceEventBus.emitSpeechLog(SpeechLogEntry(partialText))
                    processSpeech(partialText)
                }
            }

            override fun onResult(text: String) {
                // Ignore empty results
                if (text.isEmpty() || text == "{\"text\" : \"\"}") return

                serviceScope.launch {
                    val resultText = extractJsonText(text, "text")
                    ServiceEventBus.emitSpeechLog(SpeechLogEntry(resultText))
                    processSpeech(resultText)
                }
            }

            override fun onError(e: Exception) {
                ServiceEventBus.updateStatus(ServiceStatus.ERROR)
                // Retry logic after 1s
                serviceScope.launch {
                    delay(1000)
                    startListening()
                }
            }
        })
    }

    private suspend fun processSpeech(text: String) {
        val trigger = triggerParser.parse(text) ?: return
        
        ServiceEventBus.emitTrigger(trigger)
        val card = cardRepository.getCard(trigger.position) ?: return
        
        val pattern = hapticEncoder.encode(card, currentHapticConfig) ?: return
        hapticPlayer.vibrate(pattern)
    }

    private fun observeSettings() {
        serviceScope.launch {
            dataStore.debounceSec.collectLatest { sec ->
                triggerParser.setDebounce(sec)
            }
        }
        
        serviceScope.launch {
            dataStore.speedPreset.collectLatest { preset ->
                currentHapticConfig = when (preset) {
                    "FAST" -> HapticConfig(80, 200, 100, 350)
                    "SLOW" -> HapticConfig(150, 400, 200, 600)
                    else -> HapticConfig(100, 300, 150, 500)
                }
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

    private fun extractJsonText(json: String, key: String): String {
        return try {
            JSONObject(json).getString(key)
        } catch (e: Exception) {
            ""
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
    private fun <T1, T2, R> combine(flow1: kotlinx.coroutines.flow.Flow<T1>, flow2: kotlinx.coroutines.flow.Flow<T2>, transform: suspend (T1, T2) -> R): kotlinx.coroutines.flow.Flow<R> {
        return kotlinx.coroutines.flow.combine(flow1, flow2, transform)
    }
}
