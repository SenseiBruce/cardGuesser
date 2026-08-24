package com.magic.haptic.util

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class ReferenceDeckFormatterTest {
    @Test
    fun format_emptyDeck() {
        assertThat(ReferenceDeckFormatter.format(emptyList()))
            .isEqualTo("Magic Haptic cheat sheet: empty deck")
    }

    @Test
    fun format_includesPositionCardAndPattern() {
        val text =
            ReferenceDeckFormatter.format(
                listOf(
                    ReferenceDeckRow(1, "AS", "S"),
                    ReferenceDeckRow(2, "2H", "L L"),
                ),
            )
        assertThat(text).contains("2 cards")
        assertThat(text).contains("#01 AS — S")
        assertThat(text).contains("#02 2H — L L")
    }
}
