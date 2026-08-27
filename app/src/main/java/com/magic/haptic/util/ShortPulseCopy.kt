package com.magic.haptic.util

object ShortPulseCopy {
    fun clipboardText(raw: CharSequence?): String {
        val cleaned = raw?.toString()?.trim().orEmpty()
        val ms = cleaned.toLongOrNull()
        if (ms == null || ms < 0) {
            return "Short pulse: none"
        }
        return "Short pulse: ${ms}ms"
    }
}
