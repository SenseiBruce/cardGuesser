package com.magic.haptic.util

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class SeparatorGapCopyTest {
    @Test
    fun clipboardText_formatsSeparatorGap() {
        assertThat(SeparatorGapCopy.clipboardText(null)).isEqualTo("Separator gap: none")
        assertThat(SeparatorGapCopy.clipboardText(" 220 ")).isEqualTo("Separator gap: 220ms")
    }
}
