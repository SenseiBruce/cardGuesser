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
        val validation = SpeechInputSchema.validateDebounceSeconds(seconds)
        require(validation.isValid) { validation.reason ?: "invalid debounce" }
        this.debounceMs = seconds.toLong() * 1000
    }

    /**
     * Parses speech text for a deck position trigger using [SpeechInputSchema].
     */
    fun parse(text: String): TriggerResult? {
        if (!SpeechInputSchema.validateSpeechText(text).isValid) return null

        val trimmedText = text.trim()
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastTriggerTime < debounceMs) return null

        for (pattern in patterns) {
            val match = pattern.find(trimmedText) ?: continue
            val groupValue = match.groupValues[1].trim()
            if (groupValue.isEmpty() || groupValue.length > SpeechInputSchema.MAX_CAPTURE_LENGTH) continue

            val position = wordConverter.convert(groupValue) ?: continue
            if (!SpeechInputSchema.validatePosition(position).isValid) continue

            lastTriggerTime = currentTime
            return TriggerResult(position, text)
        }
        return null
    }

    companion object {
        const val MIN_POSITION = SpeechInputSchema.MIN_POSITION
        const val MAX_POSITION = SpeechInputSchema.MAX_POSITION
        const val MAX_INPUT_LENGTH = SpeechInputSchema.MAX_LENGTH
        const val MAX_CAPTURE_LENGTH = SpeechInputSchema.MAX_CAPTURE_LENGTH

        fun isValidInput(text: String): Boolean = SpeechInputSchema.validateSpeechText(text).isValid
    }
}
