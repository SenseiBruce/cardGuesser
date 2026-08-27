package com.magic.haptic.util

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class LongPulseCopyTest {
    @Test
    fun clipboardText_formatsLongPulse() {
        assertThat(LongPulseCopy.clipboardText(null)).isEqualTo("Long pulse: none")
        assertThat(LongPulseCopy.clipboardText(" 300 ")).isEqualTo("Long pulse: 300ms")
    }
}
