package com.magic.haptic.speech

import android.content.Context
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
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
        StorageService.unpack(context, "model-en-us", "model",
            { model: Model? ->
                this.model = model
                if (model != null) {
                    try {
                        recognizer = Recognizer(model, 16000.0f)
                        callback(true)
                    } catch (e: Exception) {
                        callback(false)
                    }
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
