package com.magic.haptic.card

object ReferenceDeckFormatter {
    fun format(
        deckName: String,
        cards: List<String>,
    ): String {
        val lines = ArrayList<String>(cards.size + 1)
        lines.add("Magic Haptic — $deckName")
        cards.forEachIndexed { index, card ->
            lines.add("%02d %s".format(index + 1, card))
        }
        return lines.joinToString("\n")
    }
}
