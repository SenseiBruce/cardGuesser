package com.magic.haptic.util

object ListenLabelCopy {
    fun clipboardText(rawLabel: CharSequence?): String {
        val cleaned = rawLabel?.toString()?.trim().orEmpty()
        if (cleaned.isEmpty()) {
            return "Listen control: unknown"
        }
        return "Listen control: $cleaned"
    }
}
