package com.magic.haptic.card

import com.magic.haptic.data.Rank
import com.magic.haptic.data.Suit

object DeckValidator {

    fun validate(deckString: String): ValidationResult {
        val cards = deckString.split(",")
            .map { it.trim().uppercase() }
            .filter { it.isNotEmpty() }

        if (cards.size != 52) {
            return ValidationResult(false, "Invalid count: ${cards.size} cards (must be 52)")
        }

        val uniqueCards = cards.toSet()
        if (uniqueCards.size != 52) {
            val duplicates = cards.groupBy { it }.filter { it.value.size > 1 }.keys
            return ValidationResult(false, "Duplicate cards found: $duplicates")
        }

        val invalidCards = cards.filter { !isValidCard(it) }
        if (invalidCards.isNotEmpty()) {
            return ValidationResult(false, "Invalid card strings found: $invalidCards")
        }

        return ValidationResult(true, "✓ Valid deck (52 cards)")
    }

    private fun isValidCard(card: String): Boolean {
        val rankPart = if (card.startsWith("10")) "10" else card.take(1)
        val suitPart = card.substring(rankPart.length)

        val rankExists = Rank.values().any { it.symbol == rankPart }
        val suitExists = Suit.values().any { it.symbol == suitPart }

        return rankExists && suitExists
    }

    data class ValidationResult(val isValid: Boolean, val message: String)
}
