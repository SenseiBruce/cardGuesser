package com.magic.haptic.util

import org.junit.Assert.assertEquals
import org.junit.Test

class LastPhraseCopyTest {
    @Test
    fun clipboardText_extractsQuotedPhrase() {
        assertEquals(
            "Last phrase: ace of spades",
            LastPhraseCopy.clipboardText("Phrase: \"ace of spades\""),
        )
    }

    @Test
    fun clipboardText_handlesEmptyPlaceholder() {
        assertEquals("Last phrase: none", LastPhraseCopy.clipboardText("Phrase: -"))
        assertEquals("Last phrase: none", LastPhraseCopy.clipboardText(""))
    }
}
