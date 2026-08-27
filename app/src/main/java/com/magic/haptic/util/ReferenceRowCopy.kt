package com.magic.haptic.util

object ReferenceRowCopy {
    fun clipboardText(position: Int, cardName: String, patternDesc: String): String {
        val card = cardName.trim().ifEmpty { "unknown" }
        val pattern = patternDesc.trim().ifEmpty { "--" }
        return "#${position.toString().padStart(2, '0')} $card — $pattern"
    }
}
