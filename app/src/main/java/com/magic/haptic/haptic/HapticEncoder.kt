package com.magic.haptic.haptic

import com.magic.haptic.data.Card
import com.magic.haptic.data.HapticConfig
import com.magic.haptic.data.HapticPattern
import com.magic.haptic.data.Rank
import com.magic.haptic.data.Suit

class HapticEncoder {

    fun encode(cardString: String, config: HapticConfig): HapticPattern? {
        val card = parseCardString(cardString) ?: return null
        
        val timings = mutableListOf<Long>()
        val amplitudes = mutableListOf<Int>()
        
        // Always start with 0 delay
        timings.add(0)
        amplitudes.add(0)

        // 1. Encode Rank
        val rankPulses = getRankPulses(card.rank)
        addPulses(timings, amplitudes, rankPulses, config)

        // 2. Add Separator
        timings.add(config.separatorDuration)
        amplitudes.add(0)

        // 3. Encode Suit
        val suitPulses = getSuitPulses(card.suit)
        addPulses(timings, amplitudes, suitPulses, config)

        val description = buildString {
            rankPulses.forEach { append(if (it == PulseType.SHORT) "S" else "L") }
            append("|")
            suitPulses.forEach { append(if (it == PulseType.SHORT) "S" else "L") }
        }

        return HapticPattern(
            timings = timings.toLongArray(),
            amplitudes = amplitudes.toIntArray(),
            durationMs = timings.sum(),
            description = description
        )
    }

    private fun addPulses(
        timings: MutableList<Long>,
        amplitudes: MutableList<Int>,
        pulses: List<PulseType>,
        config: HapticConfig
    ) {
        pulses.forEachIndexed { index, pulse ->
            val duration = when (pulse) {
                PulseType.SHORT -> config.shortDuration
                PulseType.LONG -> config.longDuration
            }
            
            // Add the vibration pulse
            timings.add(duration)
            amplitudes.add(255) // Max amplitude

            // Add the gap if not the last pulse in the group
            if (index < pulses.size - 1) {
                timings.add(config.gapDuration)
                amplitudes.add(0)
            }
        }
    }

    private fun getRankPulses(rank: Rank): List<PulseType> {
        return when (rank) {
            Rank.A -> listOf(PulseType.SHORT)
            Rank.TWO -> listOf(PulseType.SHORT, PulseType.SHORT)
            Rank.THREE -> listOf(PulseType.SHORT, PulseType.SHORT, PulseType.SHORT)
            Rank.FOUR -> listOf(PulseType.SHORT, PulseType.LONG)
            Rank.FIVE -> listOf(PulseType.LONG)
            Rank.SIX -> listOf(PulseType.LONG, PulseType.SHORT)
            Rank.SEVEN -> listOf(PulseType.LONG, PulseType.SHORT, PulseType.SHORT)
            Rank.EIGHT -> listOf(PulseType.LONG, PulseType.SHORT, PulseType.SHORT, PulseType.SHORT)
            Rank.NINE -> listOf(PulseType.LONG, PulseType.SHORT, PulseType.LONG)
            Rank.TEN -> listOf(PulseType.LONG, PulseType.LONG)
            Rank.J -> listOf(PulseType.SHORT, PulseType.LONG, PulseType.LONG)
            Rank.Q -> listOf(PulseType.SHORT, PulseType.SHORT, PulseType.LONG)
            Rank.K -> listOf(PulseType.LONG, PulseType.LONG, PulseType.LONG)
        }
    }

    private fun getSuitPulses(suit: Suit): List<PulseType> {
        return when (suit) {
            Suit.SPADES -> listOf(PulseType.SHORT, PulseType.LONG, PulseType.SHORT)
            Suit.HEARTS -> listOf(PulseType.SHORT, PulseType.SHORT, PulseType.SHORT, PulseType.SHORT)
            Suit.DIAMONDS -> listOf(PulseType.LONG, PulseType.LONG, PulseType.SHORT)
            Suit.CLUBS -> listOf(PulseType.SHORT, PulseType.SHORT, PulseType.SHORT, PulseType.LONG)
        }
    }

    private fun parseCardString(cardString: String): Card? {
        if (cardString.isEmpty()) return null
        
        val rankPart = if (cardString.startsWith("10")) "10" else cardString.take(1)
        val suitPart = cardString.substring(rankPart.length)

        val rank = Rank.values().find { it.symbol == rankPart } ?: return null
        val suit = Suit.values().find { it.symbol == suitPart } ?: return null

        return Card(rank, suit)
    }

    private enum class PulseType { SHORT, LONG }
}
