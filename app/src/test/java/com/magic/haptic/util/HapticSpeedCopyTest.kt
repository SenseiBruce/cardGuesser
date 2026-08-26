package com.magic.haptic.util

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class HapticSpeedCopyTest {
    @Test
    fun clipboardText_formatsSpeed() {
        assertThat(HapticSpeedCopy.clipboardText(null)).isEqualTo("Haptic speed: none")
        assertThat(HapticSpeedCopy.clipboardText(" FAST ")).isEqualTo("Haptic speed: FAST")
    }
}
