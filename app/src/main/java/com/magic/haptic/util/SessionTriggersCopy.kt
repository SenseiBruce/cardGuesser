package com.magic.haptic.util

object SessionTriggersCopy {
    fun clipboardText(rawCount: CharSequence?): String {
        val cleaned = rawCount?.toString()?.trim().orEmpty()
        val count = cleaned.toIntOrNull()
        if (count == null) {
            return "Session triggers: 0"
        }
        return "Session triggers: $count"
    }
}
