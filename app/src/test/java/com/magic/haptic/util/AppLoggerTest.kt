package com.magic.haptic.util

import com.google.common.truth.Truth.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test

class AppLoggerTest {
    @Before
    fun setUp() {
        CrashReporter.resetForTests()
    }

    @After
    fun tearDown() {
        CrashReporter.resetForTests()
    }

    @Test
    fun formatMessage_appendsStructuredFields() {
        val formatted =
            AppLogger.formatMessage(
                "speech_recognition_error",
                mapOf(
                    "event" to "speech_error",
                    "sessionId" to "abc-123",
                    "errorType" to "IOException",
                ),
            )
        assertThat(formatted).contains("speech_recognition_error")
        assertThat(formatted).contains("event=speech_error")
        assertThat(formatted).contains("sessionId=abc-123")
        assertThat(formatted).contains("errorType=IOException")
    }

    @Test
    fun formatMessage_withoutFields_returnsMessage() {
        assertThat(AppLogger.formatMessage("hello")).isEqualTo("hello")
    }
}
