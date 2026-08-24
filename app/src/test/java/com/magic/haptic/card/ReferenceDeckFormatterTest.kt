package com.magic.haptic.card

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class ReferenceDeckFormatterTest {
    @Test
    fun format_includesHeaderAndNumberedCards() {
        val text = ReferenceDeckFormatter.format("Mnemonica", listOf("4C", "2H", "7D"))
        assertThat(text).isEqualTo(
            """
            Magic Haptic — Mnemonica
            01 4C
            02 2H
            03 7D
            """.trimIndent(),
        )
    }

    @Test
    fun format_numbersAFullPresetDeck() {
        val text = ReferenceDeckFormatter.format("DEFAULT", DeckPresets.DEFAULT)
        val lines = text.lines()
        assertThat(lines[0]).isEqualTo("Magic Haptic — DEFAULT")
        assertThat(lines).hasSize(53)
        assertThat(lines[1]).isEqualTo("01 AS")
        assertThat(lines.last()).isEqualTo("52 KC")
    }
}
