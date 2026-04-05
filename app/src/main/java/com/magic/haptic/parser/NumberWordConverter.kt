package com.magic.haptic.parser

import java.util.Locale

class NumberWordConverter {

    private val units = mapOf(
        "zero" to 0, "one" to 1, "two" to 2, "three" to 3, "four" to 4,
        "five" to 5, "six" to 6, "seven" to 7, "eight" to 8, "nine" to 9,
        "ten" to 10, "eleven" to 11, "twelve" to 12, "thirteen" to 13,
        "fourteen" to 14, "fifteen" to 15, "sixteen" to 16, "seventeen" to 17,
        "eighteen" to 18, "nineteen" to 19
    )

    private val tens = mapOf(
        "twenty" to 20, "thirty" to 30, "forty" to 40, "fifty" to 50
    )

    private val ordinals = mapOf(
        "first" to 1, "second" to 2, "third" to 3, "fourth" to 4, "fifth" to 5,
        "sixth" to 6, "seventh" to 7, "eighth" to 8, "ninth" to 9, "tenth" to 10,
        "eleventh" to 11, "twelfth" to 12, "thirteenth" to 13, "fourteenth" to 14,
        "fifteenth" to 15, "sixteenth" to 16, "seventeenth" to 17, "eighteenth" to 18,
        "nineteenth" to 19, "twentieth" to 20, "thirtieth" to 30, "fortieth" to 40, "fiftieth" to 50
    )

    fun convert(text: String): Int? {
        val cleanText = text.lowercase(Locale.ROOT)
            .replace(Regex("([a-z])-([a-z])"), "$1 $2")
            .trim()
            
        // 1. Try finding numeric digits anywhere
        val digitMatch = Regex("-?\\d+").find(cleanText)
        if (digitMatch != null) {
            return digitMatch.value.toIntOrNull()
        }

        // Split text into words to scan for numbers anywhere in the phrase
        val words = cleanText.split("\\s+".toRegex())

        for (i in words.indices) {
            // 2. Try compound "thirty five"
            if (i < words.size - 1) {
                val tenWord = words[i]
                val unitWord = words[i + 1]
                
                if (tens.containsKey(tenWord)) {
                    val tenValue = tens[tenWord]!!
                    val unitValue = units[unitWord] ?: ordinals[unitWord]
                    if (unitValue != null) {
                        return tenValue + unitValue
                    }
                }
                
                // 3. Try two single digits "three five" -> 35
                if (units.containsKey(tenWord) && (units.containsKey(unitWord) || tenWord == "zero")) {
                    val d1 = units[tenWord] ?: 0
                    val d2 = units[unitWord] ?: 0
                    if (d1 in 0..9 && d2 in 0..9) {
                        return d1 * 10 + d2
                    }
                }
            }
            
            // 4. Try single words
            val word = words[i]
            ordinals[word]?.let { return it }
            tens[word]?.let { return it }
            units[word]?.let { return it }
        }

        return null
    }
}
