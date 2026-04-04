package com.magic.haptic

import com.google.common.truth.Truth.assertThat
import com.magic.haptic.data.HapticConfig
import com.magic.haptic.haptic.HapticEncoder
import org.junit.Test

class HapticEncoderTest {

    private val encoder = HapticEncoder()
    private val config = HapticConfig(100, 300, 150, 500)

    @Test
    fun testAceOfSpadesEncoding() {
        val pattern = encoder.encode("AS", config)
        assertThat(pattern).isNotNull()
        // AS = A (S) + SEP + S (L)
        // Timings: [0, 100, 500, 300]
        assertThat(pattern?.timings?.toList()).containsExactly(0L, 100L, 500L, 300L).inOrder()
        assertThat(pattern?.amplitudes?.toList()).containsExactly(0, 255, 0, 255).inOrder()
    }

    @Test
    fun testQueenOfDiamondsEncoding() {
        val pattern = encoder.encode("QD", config)
        assertThat(pattern).isNotNull()
        // QD = Q (S, S, L) + SEP + D (S, L)
        // Timings: [0, 100, 150, 100, 150, 300, 500, 100, 150, 300]
        assertThat(pattern?.timings?.toList()).containsExactly(0L, 100L, 150L, 100L, 150L, 300L, 500L, 100L, 150L, 300L).inOrder()
    }

    @Test
    fun testInvalidCardReturnsNull() {
        val pattern = encoder.encode("XX", config)
        assertThat(pattern).isNull()
    }

    @Test
    fun testTenOfHeartsEncoding() {
        val pattern = encoder.encode("10H", config)
        assertThat(pattern).isNotNull()
        // 10 = L, L
        // H = S, S
        assertThat(pattern?.timings?.toList()).contains(300L) // Long
        assertThat(pattern?.timings?.toList()).contains(100L) // Short
    }
}
