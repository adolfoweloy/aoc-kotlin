
val rule = mapOf(
    "red" to 12,
    "green" to 13,
    "blue" to 14
)

fun main() {
    fun part1(input: List<String>): Int {
        val games = input.map { Row(it).parseGame() }
        return games.filterNot { it.cracks() }.sumOf { it.id }
    }

    fun part2(input: List<String>) = 9

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day02_test")
    check(part1(testInput) == 8)

    val input = readInput("Day02")
    part1(input).println()
    part2(input).println()
}

fun Game.cracks() = subsets.any { it.cracks() }

data class Row(val value: String)

typealias Subsets = List<CubeSet>
typealias CubeSet = Pair<String, Int>

data class Game(val id: Int, val subsets: List<Subsets>)

fun Subsets.cracks() = any {
    val maxPossibleCubes = rule[it.first]
    if (maxPossibleCubes != null) {
        it.cracks(maxPossibleCubes)
    } else {
        true
    }
}

fun CubeSet.cracks(possibleAmount: Int): Boolean = second > possibleAmount

fun Row.parseGame(): Game {
    val text = value.split(":")
    return Game(
        text[0].split(" ")[1].toInt(),
        text[1].game())
}

fun String.game(): List<Subsets> = subsetsOfCubesString().map { it.parseSubsets() }

fun String.subsetsOfCubesString() = split(";")

fun String.parseSubsets(): Subsets = splitSubsetString().map { it.parseSubset() }

fun String.splitSubsetString() = split(",")

fun String.parseSubset(): CubeSet = mapCubeColorToSubsetSize(
    fold(listOf("", "")) { acc, c -> parseSubsetOfCubes(acc, c) }
)

fun mapCubeColorToSubsetSize(listDraw: List<String>): CubeSet = listDraw[1] to listDraw[0].toInt()

fun parseSubsetOfCubes(acc: List<String>, c: Char) =
    when {
        c.isDigit() -> listOf(acc[0] + c.toString(), acc[1])
        c == ' ' -> acc
        else -> listOf(acc[0], acc[1] + c)
    }

