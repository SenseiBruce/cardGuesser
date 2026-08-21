package com.magic.haptic.service

import com.magic.haptic.card.CardRepository
import com.magic.haptic.data.HapticConfig
import com.magic.haptic.data.HapticPattern
import com.magic.haptic.data.SpeechLogEntry
import com.magic.haptic.data.TriggerResult
import com.magic.haptic.haptic.HapticEncoder
import com.magic.haptic.parser.TriggerParser
import com.magic.haptic.speech.SpeechJsonExtractor

/**
 * Pure speech→trigger→card→pattern pipeline extracted from [AudioListenerService]
 * so JVM unit tests can exercise the mapping without Android Service lifecycle.
 */
class SpeechProcessor(
    private val triggerParser: TriggerParser,
    private val cardRepository: CardRepository,
    private val hapticEncoder: HapticEncoder,
) {
    data class ProcessResult(
        val trigger: TriggerResult,
        val card: String,
        val pattern: HapticPattern,
        val logEntry: SpeechLogEntry,
    )

    fun process(
        text: String,
        hapticConfig: HapticConfig,
    ): ProcessResult? {
        val trigger = triggerParser.parse(text) ?: return null
        val card = cardRepository.getCard(trigger.position) ?: return null
        val pattern = hapticEncoder.encode(card, hapticConfig) ?: return null
        return ProcessResult(
            trigger = trigger,
            card = card,
            pattern = pattern,
            logEntry = SpeechLogEntry(text, isMatch = true),
        )
    }

    fun extractAndProcess(
        rawJson: String,
        key: String,
        hapticConfig: HapticConfig,
    ): ProcessResult? {
        val text = SpeechJsonExtractor.extract(rawJson, key)
        if (text.isBlank()) return null
        return process(text, hapticConfig)
    }
}
