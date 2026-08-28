package com.magic.haptic.util

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class IdentificationRoundCopyTest {
    @Test
    fun clipboardText_formatsRound() {
        assertThat(IdentificationRoundCopy.clipboardText(null, null))
            .isEqualTo("Identification round: none")
        assertThat(IdentificationRoundCopy.clipboardText(" AH ", listOf("AH", " 2C ", "")))
            .isEqualTo("Identification round: AH (AH, 2C)")
    }
}
