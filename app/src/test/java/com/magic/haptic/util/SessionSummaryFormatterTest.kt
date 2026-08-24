package com.magic.haptic.util

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class SessionSummaryFormatterTest {
    @Test
    fun format_includesDurationTriggersAndLastDetection() {
        val text =
            SessionSummaryFormatter.format(
                duration = "00:01:05",
                triggerCount = "3",
                lastCard = "AS",
                lastPhrase = "Phrase: \"ace of spades\"",
            )
        assertThat(text).contains("Duration: 00:01:05")
        assertThat(text).contains("Triggers: 3")
        assertThat(text).contains("Last card: AS")
        assertThat(text).contains("ace of spades")
    }
}
