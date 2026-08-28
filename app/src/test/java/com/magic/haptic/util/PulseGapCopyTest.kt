package com.magic.haptic.util

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class PulseGapCopyTest {
    @Test
    fun clipboardText_formatsPulseGap() {
        assertThat(PulseGapCopy.clipboardText(null)).isEqualTo("Pulse gap: none")
        assertThat(PulseGapCopy.clipboardText(" 150 ")).isEqualTo("Pulse gap: 150ms")
    }
}
