package com.magic.haptic.util

object PulseGapCopy {
    fun clipboardText(raw: CharSequence?): String {
        val cleaned = raw?.toString()?.trim().orEmpty()
        val ms = cleaned.toLongOrNull()
        if (ms == null || ms < 0) {
            return "Pulse gap: none"
        }
        return "Pulse gap: ${ms}ms"
    }
}
