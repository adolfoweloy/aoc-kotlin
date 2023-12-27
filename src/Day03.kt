fun main() {
    fun part1(input: List<String>) = input.asSequence().parseMappings().partNumbers()
        .sumOf { it.first.value }

    fun part2(input: List<String>) = input.asSequence().parseMappings().partNumbers()
        .groupBy(
            keySelector = { it.second },
            valueTransform = { it.first }
        )
        .filterKeys { it.value == '*' }
        .filterValues { it.size == 2 }
        .map { (_, v) -> v[0] * v[1] }
        .sum()

    // checking test inputs
    val testInput = readInput("Day03_test")
    check(part1(testInput) == 4361)
    check(part2(testInput) == 467835)

    // print solutions
    val input = readInput("Day03")
    part1(input).println()
    part2(input).println()
}

sealed class Element {
    data class Number(val rowIndex: Int, val value: Int, val range: IntRange) : Element()
    data class Symbol(val rowIndex: Int, val value: Char, val position: Int) : Element()
    data class Separator(val value: String = "") : Element()
}
typealias NumberWithSymbol = Pair<Element.Number, Element.Symbol>
typealias NumbersWithSymbols = List<NumberWithSymbol>

operator fun Element.Number.times(other: Element.Number) = this.value * other.value

fun Element.Number.expandedRange() =
    (range.first - 1).coerceAtLeast(0)..(range.last + 1).coerceAtMost(140)

fun Element.Number.update(element: Element.Number) = Element.Number(
    element.rowIndex,
    (this.value.toString() + element.value.toString()).toInt(),
    IntRange(
        range.first,
        element.range.last))

typealias Elements = List<Element>
typealias Numbers = List<Element.Number>

fun Elements.replaceLast(element: Element) = dropLast(1) + element
fun Elements.filterOutSeparators() = filter { it !is Element.Separator }
fun Elements.numbers() = filterIsInstance<Element.Number>()
fun Elements.symbols() = filterIsInstance<Element.Symbol>()
fun Elements.lastIfNumberOrNull() : Element.Number? = when(lastOrNull()) {
    is Element.Number -> lastOrNull() as Element.Number
    else -> null
}

fun Char.isDot() = this == '.'
fun Char.number(rowIndex: Int, idx: Int) = Element.Number(rowIndex, digitToInt(), idx..idx)
fun Char.symbol(rowIndex:Int, idx: Int) = Element.Symbol(rowIndex, this, idx)
fun sep() = Element.Separator()

fun String.parseMappings(rowIndex: Int): Elements = foldIndexed(listOf()) { idx, acc, c -> when {
    c.isDigit() -> acc.lastIfNumberOrNull()?.let { lastNum ->
        acc.replaceLast(lastNum.update(c.number(rowIndex, idx))) } ?: (acc + c.number(rowIndex, idx))
    c.isDot() -> acc.lastIfNumberOrNull()?.let { acc + sep() } ?:acc
    else -> acc + c.symbol(rowIndex, idx)
}}

fun Numbers.adjacentTo(symbol: Element.Symbol) = filter { symbol.position in it.expandedRange() }

// here is the meat of this solution
fun Sequence<String>.parseMappings() = mapIndexed { idx, row -> row.parseMappings(idx).filterOutSeparators() }.filter { it.isNotEmpty() }

fun Sequence<Elements>.partNumbers() = windowed(size = 2, step = 1)
    .fold<List<Elements>, NumbersWithSymbols>(emptyList()) { acc, row -> acc + computeWindow(row.first(), row.last()) }
    .distinct()

fun computeWindow(first: Elements, second: Elements): NumbersWithSymbols {
    val collectFirstAdjacent = {acc: NumbersWithSymbols, symbol: Element.Symbol -> acc + first.numbers().adjacentTo(symbol).map { it to symbol } }
    val collectSecondAdjacent = {acc: NumbersWithSymbols, symbol: Element.Symbol -> acc + second.numbers().adjacentTo(symbol).map { it to symbol } }

    val a = first.symbols().fold(emptyList(), collectFirstAdjacent) + second.symbols().fold(emptyList(), collectFirstAdjacent)
    val b = first.symbols().fold(emptyList(), collectSecondAdjacent) + second.symbols().fold(emptyList(), collectSecondAdjacent)

    return (a + b)
}



