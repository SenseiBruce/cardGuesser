package com.magic.haptic.util

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class SpeechLogCountCopyTest {
    @Test
    fun clipboardText_formatsEntryCount() {
        assertThat(SpeechLogCountCopy.clipboardText(-1)).isEqualTo("Perception log: 0 entries")
        assertThat(SpeechLogCountCopy.clipboardText(1)).isEqualTo("Perception log: 1 entry")
        assertThat(SpeechLogCountCopy.clipboardText(3)).isEqualTo("Perception log: 3 entries")
    }
}
