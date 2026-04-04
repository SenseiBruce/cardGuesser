package com.magic.haptic.parser

import com.magic.haptic.data.TriggerResult

class TriggerParser(private val wordConverter: NumberWordConverter) {

    private var lastTriggerTime: Long = 0
    private var debounceMs: Long = 3000

    private val patterns = listOf(
        Regex("card at position (.+)", RegexOption.IGNORE_CASE),
        Regex("position (.+) card", RegexOption.IGNORE_CASE),
        Regex("(.+) position", RegexOption.IGNORE_CASE),
        Regex("position number (.+)", RegexOption.IGNORE_CASE),
        Regex("card number (.+)", RegexOption.IGNORE_CASE),
        Regex("number (.+) card", RegexOption.IGNORE_CASE)
    )

    fun setDebounce(seconds: Int) {
        this.debounceMs = seconds.toLong() * 1000
    }

    fun parse(text: String): TriggerResult? {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastTriggerTime < debounceMs) return null

        for (pattern in patterns) {
            val match = pattern.find(text)
            if (match != null) {
                val groupValue = match.groupValues[1]
                val position = wordConverter.convert(groupValue)
                
                if (position != null && position in 1..52) {
                    lastTriggerTime = currentTime
                    return TriggerResult(position, text)
                }
            }
        }
        return null
    }
}
