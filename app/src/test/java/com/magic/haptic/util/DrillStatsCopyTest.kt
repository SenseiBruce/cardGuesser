package com.magic.haptic.util

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class DrillStatsCopyTest {
    @Test
    fun clipboardText_formatsScore() {
        assertThat(DrillStatsCopy.clipboardText(0, 0, 0, 0))
            .isEqualTo("Drill stats: 0/0 streak 0 (best 0)")
        assertThat(DrillStatsCopy.clipboardText(3, 5, 2, 4))
            .isEqualTo("Drill stats: 3/5 streak 2 (best 4)")
    }
}
