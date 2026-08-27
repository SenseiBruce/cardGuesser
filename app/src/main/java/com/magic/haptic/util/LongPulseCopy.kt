package com.magic.haptic.util

object LongPulseCopy {
    fun clipboardText(raw: CharSequence?): String {
        val cleaned = raw?.toString()?.trim().orEmpty()
        val ms = cleaned.toLongOrNull()
        if (ms == null || ms < 0) {
            return "Long pulse: none"
        }
        return "Long pulse: ${ms}ms"
    }
}
