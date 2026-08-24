package com.magic.haptic.util

data class ReferenceDeckRow(
    val position: Int,
    val cardName: String,
    val patternDesc: String,
)

object ReferenceDeckFormatter {
    fun format(rows: List<ReferenceDeckRow>): String {
        if (rows.isEmpty()) {
            return "Magic Haptic cheat sheet: empty deck"
        }
        return buildString {
            appendLine("Magic Haptic cheat sheet (${rows.size} cards)")
            rows.forEachIndexed { index, row ->
                val line = "#${row.position.toString().padStart(2, '0')} ${row.cardName} — ${row.patternDesc}"
                if (index == rows.lastIndex) {
                    append(line)
                } else {
                    appendLine(line)
                }
            }
        }
    }
}
