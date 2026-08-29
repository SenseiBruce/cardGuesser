package com.magic.haptic.util

object ManualPositionCopy {
    fun clipboardText(raw: CharSequence?): String {
        val cleaned = raw?.toString()?.trim().orEmpty()
        val position = cleaned.toIntOrNull()
        if (position == null || position !in 1..52) {
            return "Manual position: none"
        }
        return "Manual position: $position"
    }
}
