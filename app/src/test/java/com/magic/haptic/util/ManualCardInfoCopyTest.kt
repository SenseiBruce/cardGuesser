package com.magic.haptic.util

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class ManualCardInfoCopyTest {
    @Test
    fun clipboardText_formatsManualLookup() {
        assertThat(ManualCardInfoCopy.clipboardText(null)).isEqualTo("Manual lookup: none")
        assertThat(ManualCardInfoCopy.clipboardText("Result: --")).isEqualTo("Manual lookup: none")
        assertThat(ManualCardInfoCopy.clipboardText("Card: AS")).isEqualTo("Manual lookup: AS")
        assertThat(ManualCardInfoCopy.clipboardText("  Card: 10D  ")).isEqualTo("Manual lookup: 10D")
    }
}
