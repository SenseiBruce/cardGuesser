package com.magic.haptic.util

import com.magic.haptic.data.SpeechLogEntry
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object SpeechLogFormatter {
    fun format(
        entries: List<SpeechLogEntry>,
        locale: Locale = Locale.getDefault(),
    ): String {
        if (entries.isEmpty()) {
            return "Perception log: empty"
        }
        val sdf = SimpleDateFormat("HH:mm:ss", locale)
        return buildString {
            appendLine("Perception log (${entries.size} entries)")
            entries.forEachIndexed { index, entry ->
                val stamp = sdf.format(Date(entry.timestamp))
                val marker = if (entry.isMatch) "MATCH" else "hear"
                val line = "$stamp [$marker] ${entry.text}"
                if (index == entries.lastIndex) {
                    append(line)
                } else {
                    appendLine(line)
                }
            }
        }
    }
}
