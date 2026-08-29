package com.magic.haptic.util

object SpeechLogCountCopy {
    fun clipboardText(count: Int): String {
        val n = if (count < 0) 0 else count
        val noun = if (n == 1) "entry" else "entries"
        return "Perception log: $n $noun"
    }
}
