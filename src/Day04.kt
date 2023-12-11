
fun main() {
    fun part1(input: List<String>) = input.sumOf { processLine(it) }

    fun part2(input: List<String>) = 0

    // checking test inputs
    val testInput = readInput("Day04_test")
    check(part1(testInput) == 13)
    check(part2(testInput) == 0)

    // print solutions
    val input = readInput("Day04")
    part1(input).println()
    part2(input).println()
}

typealias NumberSet = Set<Int>

data class Card(val id: Int, val win: NumberSet, val mine: NumberSet) {
    fun findMatchingNumbers(): NumberSet = win.intersect(mine)
}

fun String.parseCard() = split(":", "|").let {
    Card(
        it.first().split(" ").last().toInt(),
        parseNumbers(it[1]),
        parseNumbers(it[2])
    )
}

fun parseNumbers(line: String): NumberSet =
    line.split(" ").map { it.trim() }.filter { it.isNotEmpty() }.map { it.toInt() }.toSet()

fun processLine(line: String): Int = line
    .parseCard()
    .findMatchingNumbers()
    .foldIndexed(initial = 0) { idx, acc, _ -> if (idx == 0) 1  else acc * 2 }
