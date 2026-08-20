package com.magic.haptic.speech

import com.magic.haptic.util.AppLogger

/**
 * Extracts recognized text fields from Vosk JSON payloads without relying on
 * Android's org.json stubs (so JVM unit tests can exercise it).
 */
object SpeechJsonExtractor {
    fun extract(
        json: String,
        key: String,
    ): String {
        if (json.isBlank() || key.isBlank()) return ""
        return try {
            val pattern =
                Regex(
                    "\"${Regex.escape(key)}\"\\s*:\\s*\"((?:\\\\.|[^\"\\\\])*)\"",
                    RegexOption.IGNORE_CASE,
                )
            val match = pattern.find(json) ?: return ""
            unescapeJsonString(match.groupValues[1])
        } catch (e: Exception) {
            AppLogger.e("Unexpected error extracting JSON key '$key'", e)
            ""
        }
    }

    private fun unescapeJsonString(value: String): String {
        return value
            .replace("\\\"", "\"")
            .replace("\\\\", "\\")
            .replace("\\n", "\n")
            .replace("\\t", "\t")
    }
}
