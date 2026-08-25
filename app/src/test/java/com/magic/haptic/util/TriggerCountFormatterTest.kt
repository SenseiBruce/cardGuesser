package com.magic.haptic.util

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class TriggerCountFormatterTest {
    @Test
    fun format_zero() {
        assertThat(TriggerCountFormatter.format(0)).isEqualTo("Triggers this session: 0")
    }

    @Test
    fun format_nonzero() {
        assertThat(TriggerCountFormatter.format(7)).contains("7")
    }
}
