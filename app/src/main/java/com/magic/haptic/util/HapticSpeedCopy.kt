package com.magic.haptic.util

object HapticSpeedCopy {
    fun clipboardText(raw: CharSequence?): String {
        val cleaned = raw?.toString()?.trim().orEmpty()
        if (cleaned.isEmpty()) {
            return "Haptic speed: none"
        }
        return "Haptic speed: $cleaned"
    }
}
