fun main() {
    fun part1(input: List<String>) = Pair(
            input.first().seeds(),
            input.drop(2).parse().maps.listOfCategoryMaps())
        .let { (seeds, categoryMaps) -> seeds.minOfOrNull { seed -> categoryMaps.findLocation(seed) }}

    fun part2(input: List<String>) = 0

    // checking test inputs
    val testInput = readInput("Day05_test")
    check(part1(testInput) == 35L)
    check(part2(testInput) == 0)

    // print solutions
    val input = readInput("Day05")
    part1(input).println() // run it and dye waiting for an OOM Exception
    part2(input).println()
}


data class CategoryEntry(val destStart: Long, val srcStart: Long, val size: Long) {
    fun findDestination(seed: Long): Long? {
        val rangeStart = srcStart..<srcStart+size
        return if (seed in rangeStart) {
            val addr = rangeStart.indexOf(seed) // too slow
            destStart + addr
        } else {
            null
        }
    }
}

typealias CategoryEntries = List<CategoryEntry>
fun CategoryEntries.getAddr(src: Long): Long = this
    .map { it.findDestination(src) }
    .filterNotNull()
    .let { it.firstOrNull() ?: src }

typealias Seed = Long
typealias ListOfCategoryEntries = List<CategoryEntries>

typealias RawMappings = List<String>
typealias RawMappingsCollection = Map<String, RawMappings>

// input parsing functions

fun String.findNumbers() = this.trim()
    .split("\\s".toRegex())
    .map { it.trim().toLong() }

fun String.seeds() = """seeds:\s(.*)""".toRegex()
    .find(this)
    ?.let {
        it.groupValues
            .last()
            .findNumbers()
    } ?: emptyList()

enum class InputType {
    CATEGORY_DESCRIPTION,
    CATEGORY_MAPPING_DATA,
    EMPTY_ROW
}

fun String.inputType() = when {
    (indexOf(':') > -1) -> InputType.CATEGORY_DESCRIPTION
    isEmpty() -> InputType.EMPTY_ROW
    else -> InputType.CATEGORY_MAPPING_DATA
}

data class RawMappingsCollectionAcc(val maps: RawMappingsCollection, val categoryDescription: String, val tmpMaps: List<String>)

fun RawMappingsCollectionAcc.newCategory(newCategoryDescription: String) = RawMappingsCollectionAcc(
    maps,
    newCategoryDescription,
    emptyList()
)

fun RawMappingsCollectionAcc.addMappings(line: String) = RawMappingsCollectionAcc(
    maps,
    categoryDescription,
    tmpMaps + line
)

fun RawMappingsCollectionAcc.moveToRawMappings() = RawMappingsCollectionAcc(
    maps + mapOf(categoryDescription to tmpMaps),
    "",
    emptyList()
)

fun List<String>.parse(): RawMappingsCollectionAcc =
    map { Pair(it.inputType(), it) }
    .fold(RawMappingsCollectionAcc(emptyMap(), "", emptyList())) { acc, (inputType, line) ->
        when (inputType) {
            InputType.CATEGORY_DESCRIPTION -> acc.newCategory(line)
            InputType.CATEGORY_MAPPING_DATA -> acc.addMappings(line)
            InputType.EMPTY_ROW -> acc.moveToRawMappings()
        }
    }.moveToRawMappings()

// transformation functions

fun RawMappings.categoryEntries(): CategoryEntries = this
    .map { it.findNumbers() }
    .map { CategoryEntry(it[0], it[1], it[2]) }

fun RawMappingsCollection.listOfCategoryMaps(): ListOfCategoryEntries = map { it.value.categoryEntries() }

fun ListOfCategoryEntries.findLocation(seed: Seed): Long = fold(seed) { src, entries -> entries.getAddr(src) }


