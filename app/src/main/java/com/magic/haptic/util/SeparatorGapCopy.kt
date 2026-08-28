package com.magic.haptic.util

object SeparatorGapCopy {
    fun clipboardText(raw: CharSequence?): String {
        val cleaned = raw?.toString()?.trim().orEmpty()
        val ms = cleaned.toLongOrNull()
        if (ms == null || ms < 0) {
            return "Separator gap: none"
        }
        return "Separator gap: ${ms}ms"
    }
}
