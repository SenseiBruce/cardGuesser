package com.magic.haptic.util

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class DeckSpinnerCopyTest {
    @Test
    fun clipboardText_formatsSpinnerSelection() {
        assertThat(DeckSpinnerCopy.clipboardText(null)).isEqualTo("Deck spinner: none")
        assertThat(DeckSpinnerCopy.clipboardText("  ARONSON  ")).isEqualTo("Deck spinner: ARONSON")
    }
}
