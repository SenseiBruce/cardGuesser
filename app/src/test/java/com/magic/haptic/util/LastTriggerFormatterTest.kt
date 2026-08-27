package com.magic.haptic.util

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class LastTriggerFormatterTest {
    @Test
    fun format_includesPositionCardPhraseAndPattern() {
        val text = LastTriggerFormatter.format(1, "AS", "ace of spades", "S")
        assertThat(text).contains("Position: 1")
        assertThat(text).contains("Card: AS")
        assertThat(text).contains("Phrase: ace of spades")
        assertThat(text).contains("Pattern: S")
    }

    @Test
    fun format_unknownCard() {
        assertThat(LastTriggerFormatter.format(2, null, "two", "--")).contains("Card: --")
    }
}
