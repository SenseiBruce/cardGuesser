package com.magic.haptic.util

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class SettingsSummaryFormatterTest {
    @Test
    fun format_includesDeckSpeedAndDisguise() {
        val text =
            SettingsSummaryFormatter.format(
                deckId = "MNEMONICA",
                speedPreset = "FAST",
                debounceSec = "4",
                notifTitle = "System Optimizer",
                notifBody = "Running...",
            )
        assertThat(text).contains("Deck: MNEMONICA")
        assertThat(text).contains("Haptic speed: FAST")
        assertThat(text).contains("Debounce: 4s")
        assertThat(text).contains("Notification title: System Optimizer")
        assertThat(text).contains("Notification body: Running...")
    }
}
