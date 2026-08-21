package com.magic.haptic.util

import timber.log.Timber

/**
 * Structured logging wrapper around Timber with a consistent tag scheme.
 */
object AppLogger {
    private const val DEFAULT_TAG = "MagicHaptic"

    fun formatMessage(
        message: String,
        fields: Map<String, Any?> = emptyMap(),
    ): String {
        if (fields.isEmpty()) return message
        val rendered =
            fields.entries.joinToString(" ") { (key, value) ->
                "$key=${value ?: "null"}"
            }
        return "$message | $rendered"
    }

    fun d(
        message: String,
        tag: String = DEFAULT_TAG,
        fields: Map<String, Any?> = emptyMap(),
    ) {
        Timber.tag(tag).d(formatMessage(message, fields))
    }

    fun i(
        message: String,
        tag: String = DEFAULT_TAG,
        fields: Map<String, Any?> = emptyMap(),
    ) {
        Timber.tag(tag).i(formatMessage(message, fields))
    }

    fun w(
        message: String,
        throwable: Throwable? = null,
        tag: String = DEFAULT_TAG,
        fields: Map<String, Any?> = emptyMap(),
    ) {
        val formatted = formatMessage(message, fields)
        if (throwable != null) {
            Timber.tag(tag).w(throwable, formatted)
        } else {
            Timber.tag(tag).w(formatted)
        }
    }

    fun e(
        message: String,
        throwable: Throwable? = null,
        tag: String = DEFAULT_TAG,
        fields: Map<String, Any?> = emptyMap(),
    ) {
        val formatted = formatMessage(message, fields)
        if (throwable != null) {
            Timber.tag(tag).e(throwable, formatted)
        } else {
            Timber.tag(tag).e(formatted)
        }
    }
}
