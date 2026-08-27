package com.magic.haptic.util

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class ShortPulseCopyTest {
    @Test
    fun clipboardText_formatsShortPulse() {
        assertThat(ShortPulseCopy.clipboardText(null)).isEqualTo("Short pulse: none")
        assertThat(ShortPulseCopy.clipboardText(" 100 ")).isEqualTo("Short pulse: 100ms")
    }
}
