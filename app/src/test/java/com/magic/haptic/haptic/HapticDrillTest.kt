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

    @Test(expected = IllegalArgumentException::class)
    fun nextRoundRejectsTinyDeck() {
        HapticDrill().nextRound(listOf("AS", "KH"))
    }
}
