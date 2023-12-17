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

data class CategoryEntry(val destStart: Long, val srcStart: Long, val size: Long)

typealias Seed = Long
typealias CategoryMap = Map<Long, Long>
typealias CategoryMaps = List<CategoryMap>
typealias CategoryEntries = List<CategoryEntry>
typealias ListOfCategoryMaps = List<CategoryMaps>

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

fun List<String>.parse() =
    map { Pair(it.inputType(), it) }
    .fold(RawMappingsCollectionAcc(emptyMap(), "", emptyList())) { acc, (inputType, line) ->
        when (inputType) {
            InputType.CATEGORY_DESCRIPTION -> acc.newCategory(line)
            InputType.CATEGORY_MAPPING_DATA -> acc.addMappings(line)
            InputType.EMPTY_ROW -> acc.moveToRawMappings()
        }
    }.moveToRawMappings()

// transformation functions

// crap! the input uses huge intervals! this solution doesn't work. 1billion entries in a map leads to OOM here :((
fun CategoryEntry.generateCategoryMap(): CategoryMap = (0..<size)
    .fold(emptyMap()) { acc, i -> acc + mapOf(srcStart + i to destStart + i) }

fun CategoryEntry.generateCategoryMutableMap(): CategoryMap {
    val result = mutableMapOf<Long, Long>()
    (0..<size).forEach {i -> result[srcStart + i] = destStart + i }
    return result
}

fun RawMappings.categoryEntries(): CategoryEntries = this
    .map { it.findNumbers() }
    .map { CategoryEntry(it[0], it[1], it[2]) }

fun RawMappingsCollection.listOfCategoryMaps(): ListOfCategoryMaps = map { it.value
    .categoryEntries()
    .generateCategoryMaps()
}

fun CategoryEntries.generateCategoryMaps(): CategoryMaps = this.map { it.generateCategoryMutableMap() }

fun fromMap(maps: CategoryMaps, input: Seed): Long = maps
    .firstOrNull { it.containsKey(input) }
    ?.let { it[input] }
    ?: input

fun ListOfCategoryMaps.findLocation(seed: Seed): Long = fold(seed) { seed, m -> fromMap(m, seed) }


