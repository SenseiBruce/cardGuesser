package com.magic.haptic.util

object LastCardCopy {
    fun clipboardText(cardLabel: String?): String {
        val cleaned = cardLabel?.trim().orEmpty()
        if (cleaned.isEmpty() || cleaned == "--" || cleaned == "??") {
            return "Last card: none"
        }
        return "Last card: $cleaned"
    }
}
