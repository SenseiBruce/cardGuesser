package com.magic.haptic.util

import com.google.common.truth.Truth.assertThat
import com.magic.haptic.data.HapticConfig
import org.junit.Test

class HapticConfigFormatterTest {
    @Test
    fun format_includesPresetAndDurations() {
        val text =
            HapticConfigFormatter.format(
                "FAST",
                HapticConfig(80, 200, 100, 400),
            )
        assertThat(text).contains("Preset: FAST")
        assertThat(text).contains("Short: 80ms")
        assertThat(text).contains("Long: 200ms")
        assertThat(text).contains("Gap: 100ms")
        assertThat(text).contains("Separator: 400ms")
    }
}
