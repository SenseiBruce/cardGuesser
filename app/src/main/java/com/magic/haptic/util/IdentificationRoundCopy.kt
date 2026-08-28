package com.magic.haptic.util

object IdentificationRoundCopy {
    fun clipboardText(target: CharSequence?, options: List<CharSequence>?): String {
        val cleaned = target?.toString()?.trim().orEmpty()
        if (cleaned.isEmpty()) {
            return "Identification round: none"
        }
        val opts =
            options
                ?.map { it.toString().trim() }
                ?.filter { it.isNotEmpty() }
                .orEmpty()
        if (opts.isEmpty()) {
            return "Identification round: $cleaned"
        }
        return "Identification round: $cleaned (${opts.joinToString(", ")})"
    }
}
