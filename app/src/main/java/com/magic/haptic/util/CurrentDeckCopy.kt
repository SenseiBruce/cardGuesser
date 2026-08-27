package com.magic.haptic.util

object CurrentDeckCopy {
    fun clipboardText(raw: CharSequence?): String {
        val cleaned = raw?.toString()?.trim().orEmpty()
        if (cleaned.isEmpty()) {
            return "Current deck: none"
        }
        return "Current deck: $cleaned"
    }
}
