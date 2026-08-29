package com.magic.haptic.util

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class ManualPositionCopyTest {
    @Test
    fun clipboardText_formatsDeckPosition() {
        assertThat(ManualPositionCopy.clipboardText(null)).isEqualTo("Manual position: none")
        assertThat(ManualPositionCopy.clipboardText("0")).isEqualTo("Manual position: none")
        assertThat(ManualPositionCopy.clipboardText(" 12 ")).isEqualTo("Manual position: 12")
    }
}
