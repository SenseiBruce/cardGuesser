package com.magic.haptic.data

class SpeechLogBuffer {
    private val entries = mutableListOf<SpeechLogEntry>()

    val size: Int get() = entries.size

    fun add(entry: SpeechLogEntry) {
        entries.add(0, entry)
    }

    fun clear() {
        entries.clear()
    }

    fun get(index: Int): SpeechLogEntry = entries[index]

    fun snapshot(): List<SpeechLogEntry> = entries.toList()
}
