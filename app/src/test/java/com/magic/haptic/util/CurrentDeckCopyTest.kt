package com.magic.haptic.util

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class CurrentDeckCopyTest {
    @Test
    fun clipboardText_formatsDeck() {
        assertThat(CurrentDeckCopy.clipboardText(null)).isEqualTo("Current deck: none")
        assertThat(CurrentDeckCopy.clipboardText("  MNEMONICA  ")).isEqualTo("Current deck: MNEMONICA")
    }
}
