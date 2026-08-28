package com.magic.haptic.util

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class CustomDeckCopyTest {
    @Test
    fun clipboardText_formatsCustomStack() {
        assertThat(CustomDeckCopy.clipboardText(null)).isEqualTo("Custom stack: none")
        assertThat(CustomDeckCopy.clipboardText("  AS,AH,AD  ")).isEqualTo("Custom stack: AS,AH,AD")
    }
}
