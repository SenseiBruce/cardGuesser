package com.magic.haptic.util

import com.magic.haptic.data.HapticConfig

object HapticConfigFormatter {
    fun format(
        preset: String,
        config: HapticConfig,
    ): String {
        return listOf(
            "Haptic config",
            "Preset: $preset",
            "Short: ${config.shortDuration}ms",
            "Long: ${config.longDuration}ms",
            "Gap: ${config.gapDuration}ms",
            "Separator: ${config.separatorDuration}ms",
        ).joinToString("\n")
    }
}
