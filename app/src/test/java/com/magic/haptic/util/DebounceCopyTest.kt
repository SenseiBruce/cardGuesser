package com.magic.haptic.util

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class DebounceCopyTest {
    @Test
    fun clipboardText_formatsDebounce() {
        assertThat(DebounceCopy.clipboardText(null)).isEqualTo("Debounce: none")
        assertThat(DebounceCopy.clipboardText(" 3 ")).isEqualTo("Debounce: 3s")
    }
}
