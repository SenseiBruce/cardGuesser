package com.magic.haptic.parser

import com.magic.haptic.data.TriggerResult

class TriggerParser(private val wordConverter: NumberWordConverter) {
    private var lastTriggerTime: Long = 0
    private var debounceMs: Long = 3000

    private val patterns =
        listOf(
            Regex("card at position (.+)", RegexOption.IGNORE_CASE),
            Regex("position (.+) card", RegexOption.IGNORE_CASE),
            Regex("(.+) position", RegexOption.IGNORE_CASE),
            Regex("position number (.+)", RegexOption.IGNORE_CASE),
            Regex("card number (.+)", RegexOption.IGNORE_CASE),
            Regex("number (.+) card", RegexOption.IGNORE_CASE),
            Regex("the number (.+)", RegexOption.IGNORE_CASE),
        )

    fun setDebounce(seconds: Int) {
        require(seconds >= 0) { "debounce seconds must be >= 0" }
        this.debounceMs = seconds.toLong() * 1000
    }

    /**
     * Parses speech text for a deck position trigger.
     * Rejects blank/oversized input and positions outside 1..52.
     */
    fun parse(text: String): TriggerResult? {
        if (!isValidInput(text)) return null

        val trimmedText = text.trim()
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastTriggerTime < debounceMs) return null

        for (pattern in patterns) {
            val match = pattern.find(trimmedText) ?: continue
            val groupValue = match.groupValues[1].trim()
            if (groupValue.isEmpty() || groupValue.length > MAX_CAPTURE_LENGTH) continue

            val position = wordConverter.convert(groupValue)
            if (position != null && position in MIN_POSITION..MAX_POSITION) {
                lastTriggerTime = currentTime
                return TriggerResult(position, text)
            }
        }
        return null
    }

    companion object {
        const val MIN_POSITION = 1
        const val MAX_POSITION = 52
        const val MAX_INPUT_LENGTH = 256
        const val MAX_CAPTURE_LENGTH = 64

        fun isValidInput(text: String): Boolean {
            val trimmed = text.trim()
            return trimmed.isNotEmpty() && trimmed.length <= MAX_INPUT_LENGTH
        }
    }
}
