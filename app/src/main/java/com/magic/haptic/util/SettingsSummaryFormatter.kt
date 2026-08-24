package com.magic.haptic.util

object SettingsSummaryFormatter {
    fun format(
        deckId: String,
        speedPreset: String,
        debounceSec: String,
        notifTitle: String,
        notifBody: String,
    ): String {
        return buildString {
            appendLine("Magic Haptic settings")
            appendLine("Deck: $deckId")
            appendLine("Haptic speed: $speedPreset")
            appendLine("Debounce: ${debounceSec}s")
            appendLine("Notification title: $notifTitle")
            append("Notification body: $notifBody")
        }
    }
}
