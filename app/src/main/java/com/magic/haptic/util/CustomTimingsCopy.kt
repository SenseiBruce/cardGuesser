package com.magic.haptic.util

object CustomTimingsCopy {
    fun clipboardText(
        shortPulse: CharSequence?,
        longPulse: CharSequence?,
        pulseGap: CharSequence?,
        separatorGap: CharSequence?,
    ): String {
        fun ms(raw: CharSequence?): String {
            val value = raw?.toString()?.trim().orEmpty().toLongOrNull()
            return if (value == null || value < 0) "none" else "${value}ms"
        }
        return "Custom timings: short ${ms(shortPulse)}, long ${ms(longPulse)}, " +
            "gap ${ms(pulseGap)}, sep ${ms(separatorGap)}"
    }
}
