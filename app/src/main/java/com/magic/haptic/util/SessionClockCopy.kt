package com.magic.haptic.util

object SessionClockCopy {
    fun clipboardText(rawClock: CharSequence?): String {
        val cleaned = rawClock?.toString()?.trim().orEmpty()
        if (cleaned.isEmpty() || cleaned == "--:--:--") {
            return "Session clock: 00:00:00"
        }
        return "Session clock: $cleaned"
    }
}
