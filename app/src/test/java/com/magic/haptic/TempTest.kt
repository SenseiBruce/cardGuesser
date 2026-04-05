package com.magic.haptic

import com.magic.haptic.parser.NumberWordConverter
import com.magic.haptic.parser.TriggerParser
import org.junit.Test
import org.junit.Assert.*

class TempTest {
    @Test
    fun testNumbers() {
        val parser = TriggerParser(NumberWordConverter())
        parser.setDebounce(0)
        println("30: " + parser.parse("the number 30")?.position)
        println("13: " + parser.parse("the number 13")?.position)
        println("35: " + parser.parse("the number 35")?.position)
        println("45: " + parser.parse("the number 45")?.position)
        
        println("word 30: " + parser.parse("the number thirty")?.position)
        println("word 13: " + parser.parse("the number thirteen")?.position)
        println("word 35: " + parser.parse("the number thirty five")?.position)
        println("word 45: " + parser.parse("the number forty five")?.position)
    }
}
