package com.magic.haptic.util

object ManualCardInfoCopy {
    fun clipboardText(raw: CharSequence?): String {
        val cleaned = raw?.toString()?.trim().orEmpty()
        if (cleaned.isEmpty() || cleaned == "Result: --") {
            return "Manual lookup: none"
        }
        val card = cleaned.removePrefix("Card:").trim()
        if (card.isEmpty() || card == "--") {
            return "Manual lookup: none"
        }
        return "Manual lookup: $card"
    }
}
