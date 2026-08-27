package com.magic.haptic.util

object SessionDuration {
    fun formatElapsedSeconds(diffSeconds: Long): String {
        val safe = if (diffSeconds < 0) 0 else diffSeconds
        val hours = safe / 3600
        val minutes = (safe % 3600) / 60
        val seconds = safe % 60
        return String.format("%02d:%02d:%02d", hours, minutes, seconds)
    }

    fun clipboardText(formatted: String): String = "Session duration: $formatted"
}
