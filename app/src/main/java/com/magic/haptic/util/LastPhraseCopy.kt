package com.magic.haptic.util

object LastPhraseCopy {
    fun clipboardText(displayed: String?): String {
        val raw = displayed?.trim().orEmpty()
        val rest =
            if (raw.startsWith("Phrase:", ignoreCase = true)) {
                raw.substringAfter(':').trim()
            } else {
                raw
            }
        val unquoted = rest.removeSurrounding("\"")
        if (unquoted.isEmpty() || unquoted == "-") {
            return "Last phrase: none"
        }
        return "Last phrase: $unquoted"
    }
}
