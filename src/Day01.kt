

fun main() {
    fun part1(input: List<String>) = input.sumOf { extractFirstAndLastDigitOf(it) }

    fun part2(input: List<String>) = input.sumOf { getCalibrationValueFrom(it) }

    val input = readInput("Day01")
    part1(input).println()
    part2(input).println()
}

/**
 * This function could probably be removed and use only getCalibrationValueFrom instead.
 */
fun extractFirstAndLastDigitOf(input: String) =
    input.first { it.isDigit() }.toString().toInt() * 10 +
            input.last { it.isDigit() }.toString().toInt()

fun getCalibrationValueFrom(input: String) =
    parseCalibrationValueFrom(
        input.withIndex().mapNotNull { (idx, letter) ->
            if (letter.isDigit()) {
                letter.digitToInt()
            } else {
                input.possibleWordsFrom(idx).firstNotNullOfOrNull { NUMBERS[it] }
            }
        }.toList()
    )

fun parseCalibrationValueFrom(numbers: List<Int>) = numbers.first() * 10 + numbers.last()

fun String.possibleWordsFrom(idx: Int) =
    (3..5)
        .map { substring(idx, (idx + it).coerceAtMost(length)) }
        .toList()

val NUMBERS = mapOf(
    "one" to 1,
    "two" to 2,
    "three" to 3,
    "four" to 4,
    "five" to 5,
    "six" to 6,
    "seven" to 7,
    "eight" to 8,
    "nine" to 9
)