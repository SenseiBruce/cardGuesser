package com.magic.haptic.haptic

import kotlin.random.Random

data class DrillRound(
    val target: String,
    val options: List<String>,
)

/**
 * Identification drill: play a haptic pattern, then pick the card from a short list.
 */
class HapticDrill(
    private val random: Random = Random.Default,
) {
    fun nextRound(
        deck: List<String>,
        optionCount: Int = 4,
    ): DrillRound {
        require(deck.size >= optionCount) { "Deck must contain at least $optionCount cards" }
        val target = deck.random(random)
        val distractors = deck.filter { it != target }.shuffled(random).take(optionCount - 1)
        val options = (listOf(target) + distractors).shuffled(random)
        return DrillRound(target = target, options = options)
    }

    fun isCorrect(
        round: DrillRound,
        guess: String,
    ): Boolean = guess.equals(round.target, ignoreCase = true)
}
