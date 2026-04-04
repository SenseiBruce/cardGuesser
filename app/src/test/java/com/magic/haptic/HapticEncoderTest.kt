package com.magic.haptic

import com.google.common.truth.Truth.assertThat
import com.magic.haptic.data.HapticConfig
import com.magic.haptic.data.Rank
import com.magic.haptic.data.Suit
import com.magic.haptic.haptic.HapticEncoder
import org.junit.Test

class HapticEncoderTest {

    private val encoder = HapticEncoder()
    private val config = HapticConfig(
        shortDuration = 100L,
        longDuration = 300L,
        gapDuration = 150L,
        separatorDuration = 500L
    )

    @Test
    fun testAceOfSpadesEncoding() {
        val pattern = encoder.encode("AS", config)
        assertThat(pattern).isNotNull()
        // AS = A (S) + SEP + S (S, L, S)
        // Timings: [0, 100, 500, 100, 150, 300, 150, 100]
        val expectedTimings = longArrayOf(0, 100, 500, 100, 150, 300, 150, 100)
        assertThat(pattern?.timings?.toList()).containsExactlyElementsIn(expectedTimings.toList()).inOrder()
    }

    @Test
    fun testAll52CardsHaveUniquePatterns() {
        val seenPatterns = mutableSetOf<String>()
        val cards = mutableListOf<String>()

        Rank.values().forEach { rank ->
            Suit.values().forEach { suit ->
                val cardStr = "${rank.symbol}${suit.symbol}"
                val pattern = encoder.encode(cardStr, config)
                assertThat(pattern).isNotNull()
                
                val patternHash = pattern!!.timings.joinToString(",") + "|" + pattern.amplitudes.joinToString(",")
                assertThat(seenPatterns).doesNotContain(patternHash)
                seenPatterns.add(patternHash)
                cards.add(cardStr)
            }
        }

        assertThat(cards.size).isEqualTo(52)
        assertThat(seenPatterns.size).isEqualTo(52)
    }

    @Test
    fun testInvalidCardReturnsNull() {
        assertThat(encoder.encode("XX", config)).isNull()
        assertThat(encoder.encode("A", config)).isNull()
        assertThat(encoder.encode("S", config)).isNull()
    }

    @Test
    fun testTenOfHeartsEncoding() {
        val pattern = encoder.encode("10H", config)
        assertThat(pattern).isNotNull()
        // 10 = L, L
        // H = S, S, S, S
        val timings = pattern!!.timings.toList()
        assertThat(timings).contains(300L) // Longs for Rank
        assertThat(timings).contains(100L) // Shorts for Suit
        assertThat(timings).contains(500L) // Separator
    }
}
