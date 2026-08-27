package com.magic.haptic.util

object LastPatternCopy {
    fun clipboardText(displayed: String?): String {
        val raw = displayed?.trim().orEmpty()
        val rest =
            if (raw.startsWith("Pattern:", ignoreCase = true)) {
                raw.substringAfter(':').trim()
            } else {
                raw
            }
        if (rest.isEmpty() || rest == "-") {
            return "Last pattern: none"
        }
        return "Last pattern: $rest"
    }
}
