package com.magic.haptic.util

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class NotifBodyCopyTest {
    @Test
    fun clipboardText_formatsBody() {
        assertThat(NotifBodyCopy.clipboardText(null)).isEqualTo("Notification body: none")
        assertThat(NotifBodyCopy.clipboardText(" Charging ")).isEqualTo("Notification body: Charging")
    }
}
