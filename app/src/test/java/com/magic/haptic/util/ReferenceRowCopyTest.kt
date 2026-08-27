package com.magic.haptic.util

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class ReferenceRowCopyTest {
    @Test
    fun clipboardText_formatsARow() {
        assertThat(ReferenceRowCopy.clipboardText(1, "AS", "long-short"))
            .isEqualTo("#01 AS — long-short")
        assertThat(ReferenceRowCopy.clipboardText(12, "  10D ", "  "))
            .isEqualTo("#12 10D — --")
    }
}
