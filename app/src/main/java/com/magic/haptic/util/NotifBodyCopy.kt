package com.magic.haptic.util

object NotifBodyCopy {
    fun clipboardText(raw: CharSequence?): String {
        val cleaned = raw?.toString()?.trim().orEmpty()
        if (cleaned.isEmpty()) {
            return "Notification body: none"
        }
        return "Notification body: $cleaned"
    }
}
