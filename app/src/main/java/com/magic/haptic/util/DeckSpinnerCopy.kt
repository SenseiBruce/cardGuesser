package com.magic.haptic.util

object DeckSpinnerCopy {
    fun clipboardText(raw: CharSequence?): String {
        val cleaned = raw?.toString()?.trim().orEmpty()
        if (cleaned.isEmpty()) {
            return "Deck spinner: none"
        }
        return "Deck spinner: $cleaned"
    }
}
