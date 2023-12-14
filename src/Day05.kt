
fun main() {
    fun part1(input: List<String>) = 0

    fun part2(input: List<String>) = 0

    // checking test inputs
    val testInput = readInput("Day05_test")
    check(part1(testInput) == 0)
    check(part2(testInput) == 0)

    // print solutions
    val input = readInput("Day05")
    part1(input).println()
    part2(input).println()
}
