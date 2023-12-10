import kotlin.math.max

val rule = mapOf(
    "red" to 12,
    "green" to 13,
    "blue" to 14
)

val colors = listOf("red", "green", "blue")

fun main() {
    fun part1(input: List<String>): Int {
        val games = input.map { Row(it).parseGame() }
        return games.filterNot { it.cracks() }.sumOf { it.id }
    }

    fun part2(input: List<String>): Int {
        val games = input.map { Row(it).parseGame() }

        fun getMapWithMaxValue(acc: Map<String, Int>, cubeSet: Map<String, Int>) =
            colors.associateWith { color -> max(acc[color] ?: 0, cubeSet[color] ?: 0)  }

        return games.sumOf {
            it.subsets.fold(initial = mapOf<String, Int>()) { acc, b -> getMapWithMaxValue(acc, b) }
                .map { (_, value) -> value }
                .reduce { acc, value -> acc * value }
        }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day02_test")
    check(part1(testInput) == 8)
    check(part2(testInput) == 2286)

    val input = readInput("Day02")
    part1(input).println()
    part2(input).println()
}

fun Game.cracks() = subsets.any { it.cracks() }

data class Row(val value: String)

typealias Subsets = Map<String, Int>
typealias RevealedPair = Pair<String, Int>

data class Game(val id: Int, val subsets: List<Subsets>)

fun Subsets.cracks() = any {
    val maxCubes = rule[it.key]
    if (maxCubes != null) {
        it.value > maxCubes
    } else {
        true
    }
}

fun Row.parseGame(): Game {
    val text = value.split(":")
    return Game(
        text[0].split(" ")[1].toInt(),
        text[1].game())
}

fun String.game(): List<Subsets> = subsetsOfCubesString().map { it.parseSubsets() }

fun String.subsetsOfCubesString() = split(";")

fun String.parseSubsets(): Subsets = splitSubsetString()
    .map { it.parseSubset() }
    .associateBy(keySelector = { it.first }, valueTransform = { it.second })

fun String.splitSubsetString() = split(",")

fun String.parseSubset(): RevealedPair = mapCubeColorToSubsetSize(
    fold(initial = listOf("", "")) { acc, c -> parseSubsetOfCubes(acc, c) }
)

const val cubes = 0
const val color = 1

fun parseSubsetOfCubes(cubesAndColor: List<String>, c: Char) =
    when {
        c.isDigit() -> listOf(cubesAndColor[cubes] + c.toString(), cubesAndColor[color])
        c == ' ' -> cubesAndColor
        else -> listOf(cubesAndColor[cubes], cubesAndColor[color] + c)
    }

fun mapCubeColorToSubsetSize(cubesAndColors: List<String>): RevealedPair = cubesAndColors[color] to cubesAndColors[cubes].toInt()