package com.magic.haptic.util

import org.junit.Assert.assertEquals
import org.junit.Test

class LastPatternCopyTest {
    @Test
    fun clipboardText_extractsPatternDescription() {
        assertEquals(
            "Last pattern: long-short-long",
            LastPatternCopy.clipboardText("Pattern: long-short-long"),
        )
    }

    @Test
    fun clipboardText_handlesEmptyPlaceholder() {
        assertEquals("Last pattern: none", LastPatternCopy.clipboardText("Pattern: -"))
        assertEquals("Last pattern: none", LastPatternCopy.clipboardText(""))
    }
}
