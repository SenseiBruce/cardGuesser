package com.magic.haptic.speech

import android.content.Context
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import org.vosk.Model
import org.vosk.Recognizer
import org.vosk.android.StorageService
import java.io.IOException
import java.io.InputStream
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
        StorageService.unpack(context, "model-en-us", "model",
            { model: Model? ->
                this.model = model
                if (model != null) {
                    recognizer = Recognizer(model, 16000.0f)
                    callback(true)
                } else {
                    callback(false)
                }
            },
            { callback(false) }
        )
    }

    fun startListening(callback: RecognitionCallback) {
        if (isListening) return
        isListening = true

        val bufferSize = AudioRecord.getMinBufferSize(16000, AudioFormat.CHANNEL_IN_MONO, AudioFormat.CHANNEL_IN_PCM_16BIT) * 2
        audioRecord = AudioRecord(
            MediaRecorder.AudioSource.VOICE_RECOGNITION,
            16000,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.CHANNEL_IN_PCM_16BIT,
            bufferSize
        )

        if (audioRecord?.state != AudioRecord.STATE_INITIALIZED) {
            callback.onError(Exception("Failed to initialize AudioRecord"))
            return
        }

        audioRecord?.startRecording()

        executor.execute {
            val buffer = ByteArray(bufferSize)
            try {
                while (isListening) {
                    val read = audioRecord?.read(buffer, 0, buffer.size) ?: 0
                    if (read > 0) {
                        if (recognizer?.acceptWaveform(buffer, read) == true) {
                            callback.onResult(recognizer?.result ?: "")
                        } else {
                            callback.onPartialResult(recognizer?.partialResult ?: "")
                        }
                    }
                }
            } catch (e: Exception) {
                callback.onError(e)
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
