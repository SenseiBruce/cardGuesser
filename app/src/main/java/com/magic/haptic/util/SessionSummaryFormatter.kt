package com.magic.haptic.util

object SessionSummaryFormatter {
    fun format(
        duration: String,
        triggerCount: String,
        lastCard: String,
        lastPhrase: String,
    ): String {
        return buildString {
            appendLine("Magic Haptic session")
            appendLine("Duration: $duration")
            appendLine("Triggers: $triggerCount")
            appendLine("Last card: $lastCard")
            append("Last phrase: $lastPhrase")
        }
    }
}
