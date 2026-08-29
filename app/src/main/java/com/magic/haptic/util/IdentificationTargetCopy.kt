package com.magic.haptic.util

object IdentificationTargetCopy {
    fun clipboardText(target: CharSequence?): String {
        val cleaned = target?.toString()?.trim().orEmpty()
        if (cleaned.isEmpty()) {
            return "Identification target: none"
        }
        return "Identification target: $cleaned"
    }
}
