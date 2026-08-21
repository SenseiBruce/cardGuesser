package com.magic.haptic.parser

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class SpeechInputSchemaTest {
    @Test
    fun validateSpeechText_acceptsNormalPhrase() {
        assertThat(SpeechInputSchema.validateSpeechText("card at position five").isValid).isTrue()
    }

    @Test
    fun validateSpeechText_rejectsBlankAndOversized() {
        assertThat(SpeechInputSchema.validateSpeechText("").isValid).isFalse()
        assertThat(SpeechInputSchema.validateSpeechText("x".repeat(300)).isValid).isFalse()
    }

    @Test
    fun validatePosition_enforcesDeckBounds() {
        assertThat(SpeechInputSchema.validatePosition(1).isValid).isTrue()
        assertThat(SpeechInputSchema.validatePosition(52).isValid).isTrue()
        assertThat(SpeechInputSchema.validatePosition(0).isValid).isFalse()
        assertThat(SpeechInputSchema.validatePosition(53).isValid).isFalse()
    }

    @Test
    fun validateDebounceSeconds_enforcesRange() {
        assertThat(SpeechInputSchema.validateDebounceSeconds(0).isValid).isTrue()
        assertThat(SpeechInputSchema.validateDebounceSeconds(60).isValid).isTrue()
        assertThat(SpeechInputSchema.validateDebounceSeconds(-1).isValid).isFalse()
        assertThat(SpeechInputSchema.validateDebounceSeconds(61).isValid).isFalse()
    }
}
