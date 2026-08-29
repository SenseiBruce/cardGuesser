package com.magic.haptic.util

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class IdentificationTargetCopyTest {
    @Test
    fun clipboardText_formatsTarget() {
        assertThat(IdentificationTargetCopy.clipboardText(null))
            .isEqualTo("Identification target: none")
        assertThat(IdentificationTargetCopy.clipboardText("  ")).isEqualTo("Identification target: none")
        assertThat(IdentificationTargetCopy.clipboardText(" AH ")).isEqualTo("Identification target: AH")
    }
}
