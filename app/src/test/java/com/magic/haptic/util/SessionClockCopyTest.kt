package com.magic.haptic.util

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class SessionClockCopyTest {
    @Test
    fun clipboardText_formatsClock() {
        assertThat(SessionClockCopy.clipboardText(null)).isEqualTo("Session clock: 00:00:00")
        assertThat(SessionClockCopy.clipboardText("--:--:--")).isEqualTo("Session clock: 00:00:00")
        assertThat(SessionClockCopy.clipboardText(" 01:02:03 ")).isEqualTo("Session clock: 01:02:03")
    }
}
