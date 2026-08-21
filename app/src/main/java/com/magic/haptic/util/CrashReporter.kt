package com.magic.haptic.util

/**
 * Lightweight crash/error reporting sink.
 * Production builds can swap in Sentry/Crashlytics; tests use [RecordingBackend].
 */
object CrashReporter {
    interface Backend {
        fun record(
            throwable: Throwable,
            fields: Map<String, String> = emptyMap(),
        )

        fun isInitialized(): Boolean
    }

    class NoOpBackend : Backend {
        override fun record(
            throwable: Throwable,
            fields: Map<String, String>,
        ) = Unit

        override fun isInitialized(): Boolean = true
    }

    class RecordingBackend : Backend {
        val events = mutableListOf<Pair<Throwable, Map<String, String>>>()

        override fun record(
            throwable: Throwable,
            fields: Map<String, String>,
        ) {
            events += throwable to fields
        }

        override fun isInitialized(): Boolean = true
    }

    @Volatile
    private var backend: Backend = NoOpBackend()

    fun init(backend: Backend = NoOpBackend()) {
        this.backend = backend
        AppLogger.i(
            "crash_reporter_initialized",
            fields = mapOf("event" to "crash_reporter_init", "backend" to backend::class.simpleName),
        )
    }

    fun record(
        throwable: Throwable,
        fields: Map<String, String> = emptyMap(),
    ) {
        backend.record(throwable, fields)
        AppLogger.e(
            "crash_reported",
            throwable,
            fields =
                fields.mapValues { it.value } +
                    mapOf(
                        "event" to "crash_report",
                        "errorType" to (throwable::class.simpleName ?: "Throwable"),
                    ),
        )
    }

    fun isInitialized(): Boolean = backend.isInitialized()

    /** Test helper to reset to a clean NoOp backend. */
    fun resetForTests() {
        backend = NoOpBackend()
    }
}
