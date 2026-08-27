package com.magic.haptic.util

object DebounceCopy {
    fun clipboardText(raw: CharSequence?): String {
        val cleaned = raw?.toString()?.trim().orEmpty()
        val sec = cleaned.toIntOrNull()
        if (sec == null || sec < 0) {
            return "Debounce: none"
        }
        return "Debounce: ${sec}s"
    }
}
