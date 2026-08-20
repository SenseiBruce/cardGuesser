package com.magic.haptic.speech

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class SpeechJsonExtractorTest {
    @Test
    fun extract_returnsPartialText() {
        val json = """{"partial":"card at position five"}"""
        assertThat(SpeechJsonExtractor.extract(json, "partial")).isEqualTo("card at position five")
    }

    @Test
    fun extract_returnsFinalText() {
        val json = """{"text":"the number twelve"}"""
        assertThat(SpeechJsonExtractor.extract(json, "text")).isEqualTo("the number twelve")
    }

    @Test
    fun extract_returnsEmptyForMissingKey() {
        val json = """{"text":"hello"}"""
        assertThat(SpeechJsonExtractor.extract(json, "partial")).isEmpty()
    }

    @Test
    fun extract_returnsEmptyForMalformedJson() {
        assertThat(SpeechJsonExtractor.extract("not-json", "text")).isEmpty()
        assertThat(SpeechJsonExtractor.extract("", "text")).isEmpty()
    }
}
