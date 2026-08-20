package com.magic.haptic.util

import timber.log.Timber

/**
 * Thin wrapper around Timber with a consistent tag scheme for the app.
 */
object AppLogger {
    private const val DEFAULT_TAG = "MagicHaptic"

    fun d(
        message: String,
        tag: String = DEFAULT_TAG,
    ) {
        Timber.tag(tag).d(message)
    }

    fun i(
        message: String,
        tag: String = DEFAULT_TAG,
    ) {
        Timber.tag(tag).i(message)
    }

    fun w(
        message: String,
        throwable: Throwable? = null,
        tag: String = DEFAULT_TAG,
    ) {
        if (throwable != null) {
            Timber.tag(tag).w(throwable, message)
        } else {
            Timber.tag(tag).w(message)
        }
    }

    fun e(
        message: String,
        throwable: Throwable? = null,
        tag: String = DEFAULT_TAG,
    ) {
        if (throwable != null) {
            Timber.tag(tag).e(throwable, message)
        } else {
            Timber.tag(tag).e(message)
        }
    }
}
