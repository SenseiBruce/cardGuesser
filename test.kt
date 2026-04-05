import java.util.Locale

class NumberWordConverter {

    private val units = mapOf(
        "zero" to 0, "one" to 1, "two" to 2, "three" to 3, "four" to 4,
        "five" to 5, "six" to 6, "seven" to 7, "eight" to 8, "nine" to 9,
        "ten" to 10, "eleven" to 11, "twelve" to 12, "thirteen" to 13,
        "fourteen" to 14, "fifteen" to 15, "sixteen" to 16, "seventeen" to 17,
        "eighteen" to 18, "nineteen" to 19
    )

    private val tens = mapOf(
        "twenty" to 20, "thirty" to 30, "forty" to 40, "fifty" to 50
    )

    private val ordinals = mapOf(
        "first" to 1, "second" to 2, "third" to 3, "fourth" to 4, "fifth" to 5,
        "sixth" to 6, "seventh" to 7, "eighth" to 8, "ninth" to 9, "tenth" to 10,
        "eleventh" to 11, "twelfth" to 12, "thirteenth" to 13, "fourteenth" to 14,
        "fifteenth" to 15, "sixteenth" to 16, "seventeenth" to 17, "eighteenth" to 18,
        "nineteenth" to 19, "twentieth" to 20, "thirtieth" to 30, "fortieth" to 40, "fiftieth" to 50
    )

    fun convert(text: String): Int? {
        val cleanText = text.lowercase(Locale.ROOT)
            .replace(Regex("([a-z])-([a-z])"), "$1 $2")
            .trim()
        
        val digitMatch = Regex("-?\\d+").find(cleanText)
        if (digitMatch != null) {
            return digitMatch.value.toIntOrNull()
        }

        ordinals[cleanText]?.let { return it }

        for ((tenWord, tenValue) in tens) {
            if (cleanText.startsWith("$tenWord ")) {
                val unitPart = cleanText.substringAfter("$tenWord ")
                ordinals[unitPart]?.let { return tenValue + it }
                units[unitPart]?.let { return tenValue + it }
            }
        }

        tens[cleanText]?.let { return it }
        units[cleanText]?.let { return it }

        return null
    }
}

fun main() {
    val conv = NumberWordConverter()
    println("30: " + conv.convert("thirty"))
    println("13: " + conv.convert("thirteen"))
    println("35: " + conv.convert("thirty five"))
    println("45: " + conv.convert("forty five"))
}
