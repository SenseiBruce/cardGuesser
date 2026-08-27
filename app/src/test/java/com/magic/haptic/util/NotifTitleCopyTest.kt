package com.magic.haptic.util

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class NotifTitleCopyTest {
    @Test
    fun clipboardText_formatsTitle() {
        assertThat(NotifTitleCopy.clipboardText(null)).isEqualTo("Notification title: none")
        assertThat(NotifTitleCopy.clipboardText(" System Update ")).isEqualTo("Notification title: System Update")
    }
}
