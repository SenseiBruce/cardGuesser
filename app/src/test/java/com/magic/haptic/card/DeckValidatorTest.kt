package com.magic.haptic.card

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class DeckValidatorTest {
    @Test
    fun validate_acceptsDefaultDeck() {
        val deck = DeckPresets.DEFAULT.joinToString(",")
        val result = DeckValidator.validate(deck)
        assertThat(result.isValid).isTrue()
        assertThat(result.message).contains("Valid deck")
    }

    @Test
    fun validate_rejectsWrongCount() {
        val result = DeckValidator.validate("AS,2S,3S")
        assertThat(result.isValid).isFalse()
        assertThat(result.message).contains("Invalid count")
    }

    @Test
    fun validate_rejectsDuplicates() {
        val cards = DeckPresets.DEFAULT.toMutableList()
        cards[1] = cards[0]
        val result = DeckValidator.validate(cards.joinToString(","))
        assertThat(result.isValid).isFalse()
        assertThat(result.message).contains("Duplicate")
    }

    @Test
    fun validate_rejectsInvalidCardStrings() {
        val cards = DeckPresets.DEFAULT.toMutableList()
        cards[0] = "XX"
        val result = DeckValidator.validate(cards.joinToString(","))
        assertThat(result.isValid).isFalse()
        assertThat(result.message).contains("Invalid card")
    }
}
