package com.magic.haptic.util

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class SessionDurationTest {
    @Test
    fun formatElapsedSeconds_padsHoursMinutesSeconds() {
        assertThat(SessionDuration.formatElapsedSeconds(0)).isEqualTo("00:00:00")
        assertThat(SessionDuration.formatElapsedSeconds(3661)).isEqualTo("01:01:01")
        assertThat(SessionDuration.formatElapsedSeconds(-5)).isEqualTo("00:00:00")
    }

    @Test
    fun clipboardText_prefixesLabel() {
        assertThat(SessionDuration.clipboardText("00:01:02")).isEqualTo("Session duration: 00:01:02")
    }
}
