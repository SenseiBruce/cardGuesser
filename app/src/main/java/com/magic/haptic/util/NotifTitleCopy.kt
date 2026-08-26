package com.magic.haptic.util

object NotifTitleCopy {
    fun clipboardText(raw: CharSequence?): String {
        val cleaned = raw?.toString()?.trim().orEmpty()
        if (cleaned.isEmpty()) {
            return "Notification title: none"
        }
        return "Notification title: $cleaned"
    }
}
