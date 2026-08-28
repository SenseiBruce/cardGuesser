package com.magic.haptic.util

object CustomDeckCopy {
    fun clipboardText(raw: CharSequence?): String {
        val cleaned = raw?.toString()?.trim().orEmpty()
        if (cleaned.isEmpty()) {
            return "Custom stack: none"
        }
        return "Custom stack: $cleaned"
    }
}
