package com.magic.haptic.util

object DrillStatsCopy {
    fun clipboardText(
        correct: Int,
        attempts: Int,
        streak: Int,
        bestStreak: Int,
    ): String = "Drill: $correct/$attempts correct, streak $streak (best $bestStreak)"
}
