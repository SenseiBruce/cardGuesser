package com.magic.haptic.util

import com.google.common.truth.Truth.assertThat
import com.magic.haptic.data.SpeechLogEntry
import org.junit.Test
import java.util.Locale

class SpeechLogFormatterTest {
    @Test
    fun format_emptyLog() {
        assertThat(SpeechLogFormatter.format(emptyList())).isEqualTo("Perception log: empty")
    }

    @Test
    fun format_includesTimestampMatchAndText() {
        val entries =
            listOf(
                SpeechLogEntry("ace of spades", timestamp = 0L, isMatch = true),
                SpeechLogEntry("hello", timestamp = 1000L, isMatch = false),
            )
        val text = SpeechLogFormatter.format(entries, Locale.US)
        assertThat(text).contains("2 entries")
        assertThat(text).contains("[MATCH] ace of spades")
        assertThat(text).contains("[hear] hello")
    }
}
