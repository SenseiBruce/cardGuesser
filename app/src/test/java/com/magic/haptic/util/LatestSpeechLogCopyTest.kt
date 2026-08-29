package com.magic.haptic.util

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class LatestSpeechLogCopyTest {
    @Test
    fun clipboardText_formatsLatestLine() {
        assertThat(LatestSpeechLogCopy.clipboardText(null)).isEqualTo("Latest perception: none")
        assertThat(LatestSpeechLogCopy.clipboardText("  ")).isEqualTo("Latest perception: none")
        assertThat(LatestSpeechLogCopy.clipboardText(" ace of spades ")).isEqualTo(
            "Latest perception: ace of spades",
        )
    }
}
