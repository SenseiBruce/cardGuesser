package com.magic.haptic.haptic

import com.google.common.truth.Truth.assertThat
import com.magic.haptic.card.DeckPresets
import org.junit.Test
import kotlin.random.Random

class HapticDrillTest {
    @Test
    fun nextRoundIncludesTargetAmongOptions() {
        val drill = HapticDrill(Random(42))
        repeat(20) {
            val round = drill.nextRound(DeckPresets.DEFAULT)
            assertThat(round.options).hasSize(4)
            assertThat(round.options).contains(round.target)
            assertThat(round.options.toSet()).hasSize(4)
        }
    }

    @Test
    fun isCorrectIgnoresCase() {
        val drill = HapticDrill()
        val round = DrillRound(target = "QD", options = listOf("AS", "QD", "KH", "2C"))
        assertThat(drill.isCorrect(round, "qd")).isTrue()
        assertThat(drill.isCorrect(round, "AS")).isFalse()
    }

    @Test
    fun recordGuessTracksStreakAndBest() {
        val drill = HapticDrill()
        val queen = DrillRound(target = "QD", options = listOf("AS", "QD", "KH", "2C"))
        val ace = DrillRound(target = "AS", options = listOf("AS", "QD", "KH", "2C"))

        assertThat(drill.recordGuess(queen, "QD")).isTrue()
        assertThat(drill.recordGuess(ace, "AS")).isTrue()
        assertThat(drill.stats.streak).isEqualTo(2)
        assertThat(drill.stats.bestStreak).isEqualTo(2)

        assertThat(drill.recordGuess(queen, "AS")).isFalse()
        assertThat(drill.stats.streak).isEqualTo(0)
        assertThat(drill.stats.correct).isEqualTo(2)
        assertThat(drill.stats.attempts).isEqualTo(3)
        assertThat(drill.stats.bestStreak).isEqualTo(2)
    }

    @Test(expected = IllegalArgumentException::class)
    fun nextRoundRejectsTinyDeck() {
        HapticDrill().nextRound(listOf("AS", "KH"))
    }
}
