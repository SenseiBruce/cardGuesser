package com.magic.haptic.util

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class CustomTimingsCopyTest {
    @Test
    fun clipboardText_formatsCustomTimings() {
        assertThat(CustomTimingsCopy.clipboardText(null, " ", "x", "-1"))
            .isEqualTo("Custom timings: short none, long none, gap none, sep none")
        assertThat(CustomTimingsCopy.clipboardText(" 100 ", "300", "150", "220"))
            .isEqualTo("Custom timings: short 100ms, long 300ms, gap 150ms, sep 220ms")
    }
}
