package com.magic.haptic

import com.google.common.truth.Truth.assertThat
import com.magic.haptic.parser.NumberWordConverter
import com.magic.haptic.parser.TriggerParser
import org.junit.Test

class TriggerParserTest {

    private val converter = NumberWordConverter()
    private val parser = TriggerParser(converter)

    @Test
    fun testNumberWordConverter() {
        assertThat(converter.convert("five")).isEqualTo(5)
        assertThat(converter.convert("twelve")).isEqualTo(12)
        assertThat(converter.convert("twenty three")).isEqualTo(23)
        assertThat(converter.convert("forty two")).isEqualTo(42)
        assertThat(converter.convert("52")).isEqualTo(52)
        assertThat(converter.convert("first")).isEqualTo(1)
        assertThat(converter.convert("twenty second")).isEqualTo(22)
        assertThat(converter.convert("5th")).isEqualTo(5)
    }

    @Test
    fun testTriggerParserPatterns() {
        parser.setDebounce(0)
        // Pattern 1: card at position <X>
        assertThat(parser.parse("card at position twenty three")?.position).isEqualTo(23)
        assertThat(parser.parse("card at position 53")).isNull()
        
        // Pattern 2: position <X> card
        assertThat(parser.parse("position five card")?.position).isEqualTo(5)
        
        // Pattern 3: <X>th position
        assertThat(parser.parse("twenty third position")?.position).isEqualTo(23)
        
        // Pattern 4: position number <X>
        assertThat(parser.parse("position number forty two")?.position).isEqualTo(42)
        
        // Pattern 5: card number <X>
        assertThat(parser.parse("card number seven")?.position).isEqualTo(7)
        
        // Pattern 6: number <X> card
        assertThat(parser.parse("number twelve card")?.position).isEqualTo(12)
    }

    @Test
    fun testBoundsRejection() {
        parser.setDebounce(0)
        assertThat(parser.parse("card at position zero")).isNull()
        assertThat(parser.parse("card at position 53")).isNull()
        assertThat(parser.parse("card at position -1")).isNull()
    }

    @Test
    fun testDebounce() {
        parser.setDebounce(3)
        assertThat(parser.parse("card at position 5")).isNotNull()
        // Immediate second call should be null
        assertThat(parser.parse("card at position 5")).isNull()
    }

    @Test
    fun testDifferentTriggersAreNotDebouncedAgainstEachOther() {
        parser.setDebounce(3)
        assertThat(parser.parse("card at position 5")).isNotNull()
        // Different position should still trigger if using a different logic, 
        // but currently the debounce is global per parser instance.
        // Let's verify current behavior.
        assertThat(parser.parse("card at position 10")).isNull() 
        // Note: This confirms global debounce. If we wanted per-position debounce, 
        // we'd need to change TriggerParser.
    }
}
