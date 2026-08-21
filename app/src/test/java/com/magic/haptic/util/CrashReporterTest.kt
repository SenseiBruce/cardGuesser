package com.magic.haptic.util

import com.google.common.truth.Truth.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test

class CrashReporterTest {
    @Before
    fun setUp() {
        CrashReporter.resetForTests()
    }

    @After
    fun tearDown() {
        CrashReporter.resetForTests()
    }

    @Test
    fun init_doesNotThrowAndMarksInitialized() {
        CrashReporter.init(CrashReporter.NoOpBackend())
        assertThat(CrashReporter.isInitialized()).isTrue()
    }

    @Test
    fun record_forwardsToBackendWithFields() {
        val backend = CrashReporter.RecordingBackend()
        CrashReporter.init(backend)
        val error = IllegalStateException("boom")
        CrashReporter.record(error, mapOf("component" to "AudioListenerService"))
        assertThat(backend.events).hasSize(1)
        assertThat(backend.events[0].first).isSameInstanceAs(error)
        assertThat(backend.events[0].second["component"]).isEqualTo("AudioListenerService")
    }
}
