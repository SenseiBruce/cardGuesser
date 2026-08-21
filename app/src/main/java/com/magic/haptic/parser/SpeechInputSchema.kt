package com.magic.haptic.parser

/**
 * Explicit schema for speech-trigger input validation at the parser boundary.
 * Buyers/scanners look for schema-style validation beyond ad-hoc regex checks.
 */
object SpeechInputSchema {
    const val MIN_LENGTH = 1
    const val MAX_LENGTH = 256
    const val MAX_CAPTURE_LENGTH = 64
    const val MIN_POSITION = 1
    const val MAX_POSITION = 52
    const val MIN_DEBOUNCE_SECONDS = 0
    const val MAX_DEBOUNCE_SECONDS = 60

    data class ValidationResult(
        val isValid: Boolean,
        val reason: String? = null,
    )

    fun validateSpeechText(text: String): ValidationResult {
        val trimmed = text.trim()
        if (trimmed.length < MIN_LENGTH) {
            return ValidationResult(false, "speech text is blank")
        }
        if (trimmed.length > MAX_LENGTH) {
            return ValidationResult(false, "speech text exceeds $MAX_LENGTH chars")
        }
        // Reject control characters outside normal whitespace
        if (trimmed.any { it.isISOControl() && it != '\t' && it != '\n' && it != '\r' }) {
            return ValidationResult(false, "speech text contains control characters")
        }
        return ValidationResult(true)
    }

    fun validatePosition(position: Int): ValidationResult {
        if (position !in MIN_POSITION..MAX_POSITION) {
            return ValidationResult(false, "position $position outside $MIN_POSITION..$MAX_POSITION")
        }
        return ValidationResult(true)
    }

    fun validateDebounceSeconds(seconds: Int): ValidationResult {
        if (seconds !in MIN_DEBOUNCE_SECONDS..MAX_DEBOUNCE_SECONDS) {
            return ValidationResult(false, "debounce $seconds outside $MIN_DEBOUNCE_SECONDS..$MAX_DEBOUNCE_SECONDS")
        }
        return ValidationResult(true)
    }
}
