package com.magic.haptic.util

import org.junit.Assert.assertEquals
import org.junit.Test

class DrillStatsCopyTest {
    @Test
    fun clipboardText_formatsScoreAndStreaks() {
        assertEquals(
            "Drill: 3/5 correct, streak 2 (best 4)",
            DrillStatsCopy.clipboardText(3, 5, 2, 4),
        )
        assertEquals(
            "Drill: 0/0 correct, streak 0 (best 0)",
            DrillStatsCopy.clipboardText(0, 0, 0, 0),
        )
    }
}
