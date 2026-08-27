package com.magic.haptic.data

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class SpeechLogBufferTest {
    @Test
    fun clear_removesNewestFirstEntries() {
        val buffer = SpeechLogBuffer()
        buffer.add(SpeechLogEntry(text = "one"))
        buffer.add(SpeechLogEntry(text = "two"))
        assertThat(buffer.size).isEqualTo(2)
        assertThat(buffer.get(0).text).isEqualTo("two")
        buffer.clear()
        assertThat(buffer.size).isEqualTo(0)
        assertThat(buffer.snapshot()).isEmpty()
    }
}
