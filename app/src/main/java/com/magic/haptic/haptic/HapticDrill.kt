package com.magic.haptic.haptic

import kotlin.random.Random

data class DrillRound(
    val target: String,
    val options: List<String>,
)

data class DrillStats(
    val correct: Int = 0,
    val attempts: Int = 0,
    val streak: Int = 0,
    val bestStreak: Int = 0,
)

/**
 * Identification drill: play a haptic pattern, then pick the card from a short list.
 */
class HapticDrill(
    private val random: Random = Random.Default,
) {
    var stats: DrillStats = DrillStats()
        private set

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

    fun recordGuess(
        round: DrillRound,
        guess: String,
    ): Boolean {
        val ok = isCorrect(round, guess)
        val streak = if (ok) stats.streak + 1 else 0
        stats =
            DrillStats(
                correct = stats.correct + if (ok) 1 else 0,
                attempts = stats.attempts + 1,
                streak = streak,
                bestStreak = maxOf(stats.bestStreak, streak),
            )
        return ok
    }

    fun restoreStats(value: DrillStats) {
        stats =
            DrillStats(
                correct = value.correct.coerceAtLeast(0),
                attempts = value.attempts.coerceAtLeast(0),
                streak = value.streak.coerceAtLeast(0),
                bestStreak = value.bestStreak.coerceAtLeast(0),
            )
    }
}
