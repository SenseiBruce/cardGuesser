package com.magic.haptic.speech

import android.content.Context
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.Manifest
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import org.vosk.Model
import org.vosk.Recognizer
import org.vosk.android.StorageService
import java.util.concurrent.Executors

class VoskRecognizerManager(private val context: Context) {

    private var model: Model? = null
    private var recognizer: Recognizer? = null
    private var audioRecord: AudioRecord? = null
    private val executor = Executors.newSingleThreadExecutor()
    private var isListening = false

    interface RecognitionCallback {
        fun onPartialResult(text: String)
        fun onResult(text: String)
        fun onError(e: Exception)
    }

    fun initModel(callback: (Boolean) -> Unit) {
        android.util.Log.i("MagicHaptic", "Starting model unpack from assets: model-en-us")
        val mainHandler = android.os.Handler(android.os.Looper.getMainLooper())
        
        StorageService.unpack(context, "model-en-us", "model",
            { model: Model? ->
                this.model = model
                if (model != null) {
                    android.util.Log.i("MagicHaptic", "Model unpacked successfully. Initializing recognizer...")
                    try {
                        recognizer = Recognizer(model, 16000.0f)
                        android.util.Log.i("MagicHaptic", "Recognizer initialized. Ready to listen.")
                        callback(true)
                    } catch (e: Exception) {
                        mainHandler.post { android.widget.Toast.makeText(context, "Voice Init Failed: ${e.message}", android.widget.Toast.LENGTH_LONG).show() }
                        android.util.Log.e("MagicHaptic", "Recognizer init FAILED: ${e.message}", e)
                        callback(false)
                    }
                } else {
                    mainHandler.post { android.widget.Toast.makeText(context, "Model not found in assets", android.widget.Toast.LENGTH_LONG).show() }
                    android.util.Log.e("MagicHaptic", "Model unpack returned null — model files may be missing from assets.")
                    callback(false)
                }
            },
            { e ->
                mainHandler.post { android.widget.Toast.makeText(context, "Unpack Error: ${e?.message}", android.widget.Toast.LENGTH_LONG).show() }
                android.util.Log.e("MagicHaptic", "StorageService.unpack FAILED: ${e?.message}", e)
                callback(false)
            }
        )
    }


    fun startListening(callback: RecognitionCallback) {
        if (isListening) return
        isListening = true

        // Verify runtime RECORD_AUDIO permission before creating AudioRecord
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            callback.onError(SecurityException("RECORD_AUDIO permission not granted"))
            isListening = false
            return
        }

        val sampleRate = 16000
        val bufferSize = AudioRecord.getMinBufferSize(sampleRate, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT)
        audioRecord = AudioRecord(
            MediaRecorder.AudioSource.VOICE_RECOGNITION,
            sampleRate,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT,
            bufferSize
        )

        if (audioRecord?.state != AudioRecord.STATE_INITIALIZED) {
            callback.onError(Exception("Failed to initialize AudioRecord"))
            isListening = false
            return
        }

        audioRecord?.startRecording()

        executor.execute {
            val buffer = ShortArray(bufferSize)
            try {
                while (isListening) {
                    val read = audioRecord?.read(buffer, 0, buffer.size) ?: 0
                    if (read > 0) {
                        val rec = recognizer
                        if (rec != null) {
                            if (rec.acceptWaveForm(buffer, read)) {
                                callback.onResult(rec.getResult() ?: "")
                            } else {
                                callback.onPartialResult(rec.getPartialResult() ?: "")
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                callback.onError(e)
            } finally {
                stop()
            }
        }
    }

    fun stop() {
        isListening = false
        audioRecord?.stop()
        audioRecord?.release()
        audioRecord = null
    }

    fun release() {
        stop()
        recognizer?.close()
        model?.close()
        executor.shutdown()
    }
}
