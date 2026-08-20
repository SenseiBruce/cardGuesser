package com.magic.haptic.parser

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class NumberWordConverterTest {
    private val converter = NumberWordConverter()

    @Test
    fun convert_supportsOptionalAndBetweenTensAndUnits() {
        assertThat(converter.convert("thirty and five")).isEqualTo(35)
        assertThat(converter.convert("forty and two")).isEqualTo(42)
    }

    @Test
    fun convert_supportsHyphenatedCompoundsViaExistingCleanup() {
        assertThat(converter.convert("twenty-three")).isEqualTo(23)
    }

    @Test
    fun convert_rejectsUnknownWords() {
        assertThat(converter.convert("banana")).isNull()
    }
}
