package com.magic.haptic.util

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class ListenLabelCopyTest {
    @Test
    fun clipboardText_formatsToggleLabel() {
        assertThat(ListenLabelCopy.clipboardText(null)).isEqualTo("Listen control: unknown")
        assertThat(ListenLabelCopy.clipboardText("  ")).isEqualTo("Listen control: unknown")
        assertThat(ListenLabelCopy.clipboardText("START LISTENING"))
            .isEqualTo("Listen control: START LISTENING")
    }
}
