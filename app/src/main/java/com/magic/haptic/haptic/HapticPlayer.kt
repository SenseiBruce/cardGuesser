package com.magic.haptic.haptic

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import com.magic.haptic.data.HapticPattern

class HapticPlayer(private val context: Context) {
    private val vibrator: Vibrator? by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
            vibratorManager?.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        }
    }

    fun vibrate(pattern: HapticPattern) {
        if (vibrator?.hasVibrator() == true) {
            val effect = VibrationEffect.createWaveform(pattern.timings, pattern.amplitudes, -1)
            vibrator?.vibrate(effect)
        }
    }

    fun cancel() {
        vibrator?.cancel()
    }
}
