package com.magic.haptic.data

/** Card Rank and Suit Enums */
enum class Rank(val symbol: String) {
    A("A"), TWO("2"), THREE("3"), FOUR("4"), FIVE("5"), SIX("6"), SEVEN("7"),
    EIGHT("8"), NINE("9"), TEN("10"), J("J"), Q("Q"), K("K")
}

enum class Suit(val symbol: String) {
    SPADES("S"), HEARTS("H"), DIAMONDS("D"), CLUBS("C")
}

/** Represents a decoded card identifier */
data class Card(val rank: Rank, val suit: Suit) {
    override fun toString() = "${rank.symbol}${suit.symbol}"
}

/** Result of a successful trigger match */
data class TriggerResult(
    val position: Int,
    val rawText: String,
    val timestamp: Long = System.currentTimeMillis()
)

/** Configuration for haptic timing */
data class HapticConfig(
    val shortDuration: Long,
    val longDuration: Long,
    val gapDuration: Long,
    val separatorDuration: Long
)

/** The final pattern sent to the Android Vibrator */
data class HapticPattern(
    val timings: LongArray,
    val amplitudes: IntArray,
    val durationMs: Long
)

/** Service Status States */
enum class ServiceStatus { STOPPED, INITIALIZING, LISTENING, ERROR }

/** Speech log entry for debug display */
data class SpeechLogEntry(
    val text: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isMatch: Boolean = false
)
