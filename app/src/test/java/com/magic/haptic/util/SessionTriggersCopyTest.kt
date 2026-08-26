package com.magic.haptic.util

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class SessionTriggersCopyTest {
    @Test
    fun clipboardText_parsesCounts() {
        assertThat(SessionTriggersCopy.clipboardText(null)).isEqualTo("Session triggers: 0")
        assertThat(SessionTriggersCopy.clipboardText("--")).isEqualTo("Session triggers: 0")
        assertThat(SessionTriggersCopy.clipboardText(" 7 ")).isEqualTo("Session triggers: 7")
    }
}
