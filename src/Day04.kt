
fun main() {
    fun part1(input: List<String>) = input.sumOf { findMatches(it).countPoints() }

    // TODO: this function can be better. I should use just one data structure that satisfies both part 1 and part 2.
    fun part2(input: List<String>): Int {
        val matches = input
            .map { it.parseCard().findMatchingNumbers() }
            .associateBy(
                keySelector = { it.id },
                valueTransform = { it.matches.size }
            )

        val count = (1..input.size).associateBy(
            keySelector = { it },
            valueTransform = { 1 }
        )

        fun f(acc: Map<Int, Int>, id: Int): Map<Int, Int> {
            val p = Pair(id+1, id+(matches[id] ?: 0))
            if (p.first > p.second) return acc
            val rangeToUpdate = (p.first..p.second)
            return acc.map {(k, v) ->
                if (k in rangeToUpdate) {
                    Pair(k, v + (acc[id] ?: 0))
                } else {
                    Pair(k, v)
                }
            }.associateBy(
                keySelector = { it.first },
                valueTransform = { it.second }
            )
        }

        return matches.keys
            .fold(count) { acc, i -> f(acc, i) }
            .map { it.value }
            .sum()
    }

    // checking test inputs
    val testInput = readInput("Day04_test")
    check(part1(testInput) == 13)
    check(part2(testInput) == 30)

    // print solutions
    val input = readInput("Day04")
    part1(input).println()
    part2(input).println()
}

typealias NumberSet = Set<Int>
fun NumberSet.countPoints() = foldIndexed(initial = 0) { idx, acc, _ -> if (idx == 0) 1  else acc * 2 }

data class Card(val id: Int, val win: NumberSet, val mine: NumberSet) {
    fun findMatchingNumbers() = CardWithMatches(id, win.intersect(mine))
}

data class CardWithMatches(val id: Int, val matches: NumberSet)

fun String.parseCard() = split(":", "|").let {
    Card(
        it.first().split(" ").last().toInt(),
        parseNumbers(it[1]),
        parseNumbers(it[2])
    )
}

fun parseNumbers(line: String): NumberSet =
    line.split(" ").map { it.trim() }.filter { it.isNotEmpty() }.map { it.toInt() }.toSet()

fun findMatches(line: String): NumberSet = line
    .parseCard()
    .findMatchingNumbers()
    .matches

