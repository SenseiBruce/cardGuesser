package com.magic.haptic.util

object DrillStatsCopy {
    fun clipboardText(
        correct: Int,
        attempts: Int,
        streak: Int,
        bestStreak: Int,
    ): String {
        return "Drill stats: $correct/$attempts streak $streak (best $bestStreak)"
    }
}
