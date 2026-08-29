package com.magic.haptic.util

object NotifDisguiseCopy {
    fun clipboardText(title: CharSequence?, body: CharSequence?): String {
        fun field(raw: CharSequence?): String {
            val cleaned = raw?.toString()?.trim().orEmpty()
            return cleaned.ifEmpty { "none" }
        }
        return "Notification disguise: title ${field(title)}, body ${field(body)}"
    }
}
