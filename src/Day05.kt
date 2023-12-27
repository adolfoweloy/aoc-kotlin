fun main() {
    fun part1(input: List<String>) = input.parsePart1()
        .let { (seeds, categoryMaps) -> seeds
            .minOfOrNull { seed -> categoryMaps.findLocation(seed) }
        }

    fun part2(input: List<String>) = input.parsePart2()
            .let { (seedsRange, categoryMaps) -> seedsRange.asSequence()
                .map { fetchLocation(it, categoryMaps) }// fetching location for each range seems too slow
                .min()
            }

    // checking test inputs
    val testInput = readInput("Day05_test")
    check(part1(testInput) == 35L)
    check(part2(testInput) == 46L)

    // print solutions
    val input = readInput("Day05")
    part1(input).println() // run it and dye waiting for an OOM Exception
    part2(input).println()
}

fun List<String>.parsePart1() = Pair(
    first().seeds(),
    drop(2).parseMappings().maps.listOfCategoryMaps())

fun List<String>.parsePart2() = Pair(
    this.first().seeds()
        .chunked(2)
        .map { it.first()..it.first()+it.last() },
    this.drop(2).parseMappings().maps.listOfCategoryMaps())

fun fetchLocation(range: LongRange, categoryMaps: ListOfCategoryEntries): Long {
    return range.minOfOrNull { seed -> categoryMaps.findLocation(seed) } ?: 0
}

data class CategoryEntry(val destStart: Long, val srcStart: Long, val size: Long) {
    fun findDestination(seed: Long): Long? {
        return if (seed >= srcStart && seed<srcStart+size) {
            destStart + (seed - srcStart)
        } else {
            null
        }
    }
}

typealias CategoryEntries = List<CategoryEntry>
fun CategoryEntries.getAddress(src: Long): Long = this
    .mapNotNull { it.findDestination(src) }
    .let { it.firstOrNull() ?: src }

typealias Seed = Long
typealias ListOfCategoryEntries = List<CategoryEntries>

typealias RawMappings = List<String>
typealias RawMappingsCollection = Map<String, RawMappings>

// input parsing functions

fun String.findNumbers() = this.trim()
    .split("\\s".toRegex())
    .map { it.trim().toLong() }

fun String.seeds() = """seeds:\s(.*)"""
    .toRegex()
    .find(this)
    ?.groupValues?.last()
    ?.findNumbers()
    ?: emptyList()


enum class InputType { CATEGORY_DESCRIPTION, CATEGORY_MAPPING_DATA, EMPTY_ROW }

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

fun List<String>.parseMappings(): RawMappingsCollectionAcc =
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

fun ListOfCategoryEntries.findLocation(seed: Seed): Long = fold(seed) { src, entries -> entries.getAddress(src) }


