package com.magic.haptic.util

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class LastCardCopyTest {
    @Test
    fun clipboardText_handlesMissingAndDetectedCards() {
        assertThat(LastCardCopy.clipboardText(null)).isEqualTo("Last card: none")
        assertThat(LastCardCopy.clipboardText("--")).isEqualTo("Last card: none")
        assertThat(LastCardCopy.clipboardText("??")).isEqualTo("Last card: none")
        assertThat(LastCardCopy.clipboardText(" AS ")).isEqualTo("Last card: AS")
    }
}
