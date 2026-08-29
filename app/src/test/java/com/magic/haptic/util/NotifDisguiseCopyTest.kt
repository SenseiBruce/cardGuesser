package com.magic.haptic.util

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class NotifDisguiseCopyTest {
    @Test
    fun clipboardText_formatsTitleAndBody() {
        assertThat(NotifDisguiseCopy.clipboardText(null, "  "))
            .isEqualTo("Notification disguise: title none, body none")
        assertThat(NotifDisguiseCopy.clipboardText(" System Update ", "Installing…"))
            .isEqualTo("Notification disguise: title System Update, body Installing…")
    }
}
