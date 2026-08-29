package com.magic.haptic.util

object LatestSpeechLogCopy {
    fun clipboardText(text: CharSequence?): String {
        val cleaned = text?.toString()?.trim().orEmpty()
        if (cleaned.isEmpty()) {
            return "Latest perception: none"
        }
        return "Latest perception: $cleaned"
    }
}
