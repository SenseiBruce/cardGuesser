package com.magic.haptic.util

object LastTriggerFormatter {
    fun format(
        position: Int,
        card: String?,
        phrase: String,
        patternDescription: String,
    ): String {
        return listOf(
            "Last detected trigger",
            "Position: $position",
            "Card: ${card ?: "--"}",
            "Phrase: $phrase",
            "Pattern: $patternDescription",
        ).joinToString("\n")
    }
}
