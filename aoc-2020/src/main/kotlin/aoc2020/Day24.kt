package aoc2020

import aoc2020.Day24.part1
import aoc2020.Day24.part2
import common.readLines

private typealias HexCoordinate = Map<String, Int>
object Day24 {
    private val reduceCache: MutableMap<HexCoordinate, HexCoordinate> = mutableMapOf()

    val input = readLines("/aoc2020/day24.txt").map { it.parseLine() }

    val testInput = """
        sesenwnenenewseeswwswswwnenewsewsw
        neeenesenwnwwswnenewnwwsewnenwseswesw
        seswneswswsenwwnwse
        nwnwneseeswswnenewneswwnewseswneseene
        swweswneswnenwsewnwneneseenw
        eesenwseswswnenwswnwnwsewwnwsene
        sewnenenenesenwsewnenwwwse
        wenwwweseeeweswwwnwwe
        wsweesenenewnwwnwsenewsenwwsesesenwne
        neeswseenwwswnwswswnw
        nenwswwsewswnenenewsenwsenwnesesenew
        enewnwewneswsewnwswenweswnenwsenwsw
        sweneswneswneneenwnewenewwneswswnese
        swwesenesewenwneswnwwneseswwne
        enesenwswwswneneswsenwnewswseenwsese
        wnwnesenesenenwwnenwsewesewsesesew
        nenewswnwewswnenesenwnesewesw
        eneswnwswnwsenenwnwnwwseeswneewsenese
        neswnwewnwnwseenwseesewsenwsweewe
        wseweeenwnesenwwwswnew
    """.trimIndent().lines().map { it.parseLine() }

    fun String.parseLine(): HexCoordinate {
        fun split(accumulator: List<String>, line: String): List<String> {
            if (line.isEmpty()) return accumulator
            val first = line.slice(0 until 1)
            return when (first) {
                "s", "n"  -> split(accumulator + line.slice(0 until 2), line.slice(2 until line.length))
                "e", "w" -> split(accumulator + first, line.slice(1 until line.length))
                else -> throw AssertionError("unexpected")
            }
        }

        return split(emptyList(), this).groupingBy { it }.eachCount().reduce()
    }

    fun HexCoordinate.iterate() = shortcut("ne", "se", "e") // done
            .shortcut("nw", "sw", "w") // done
            .opposite("ne", "sw") // done
            .opposite("nw", "se") // done
            .opposite("e", "w") // done
            .shortcut("se", "w", "sw")
            .shortcut("ne", "w", "nw")
            .shortcut("sw", "e", "se")
            .shortcut("nw", "e", "ne")

    fun List<HexCoordinate>.part1(): Int {
        return counts()
                .entries.count { it.value % 2 == 1 }
    }
    val directions = listOf("e", "w", "ne", "nw", "se", "sw")
    val origin: HexCoordinate = directions.associateWith { 0 }

    private fun List<HexCoordinate>.counts() = groupingBy { it }.eachCount()
    enum class State {
        WHITE, BLACK
    }

    fun List<HexCoordinate>.part2(): Int {
        fun HexCoordinate.neighbours(): List<HexCoordinate> {
            return directions.map { (this + (it to (this.getOrDefault(it, 0) + 1))).reduce()  }
        }

        val counts = counts().mapValues {
            when (it.value % 2) {
                0 -> State.WHITE
                1 -> State.BLACK
                else -> throw AssertionError("not possible $it")
            }
        }

        fun HexCoordinate.steps() = values.sum()

        fun Map.Entry<HexCoordinate, State>.iterate(source: Map<HexCoordinate, State>): State {
            val (coord, state) = this

            val blackTiles = coord.neighbours().map { source.getOrDefault(it, State.WHITE) }
                    .count { it == State.BLACK }

            return when {
                state == State.BLACK && (blackTiles == 0 || blackTiles > 2) -> State.WHITE
                state == State.WHITE && blackTiles == 2 -> State.BLACK
                else -> state
            }
        }

        fun Set<HexCoordinate>.nextLevelCoordinates() =
                flatMap { source -> source.neighbours().filter { neighbour -> neighbour.steps() > source.steps() } }.toSet()

        fun Map<HexCoordinate, State>.step(): Map<HexCoordinate, State> {
            // generate full list of neighbours to check
            val maxStepsSeen = this.keys.map { it.steps() }.max()!!
            val coordinatesAtLevelMaxSteps = this.keys.filter { it.steps() == maxStepsSeen }.toSet()

            val maxPlusOne = coordinatesAtLevelMaxSteps.nextLevelCoordinates()

            val everything = (this + (maxPlusOne).associateWith { this.getOrDefault(it, State.WHITE) })

            return everything.mapValues { it.iterate(everything) }
        }

        println("filling")
        val inProgressGrid = counts.toMutableMap()
        val fullGrid = generateSequence(setOf(origin)) { currentLevelCoordinates -> currentLevelCoordinates.nextLevelCoordinates() }
                .withIndex()
                .onEach { println("${it.index} -> ${it.value}") }
                .map { it.value }
                .take(20)
                .flatten()
                .forEach { inProgressGrid[it] = inProgressGrid.getOrDefault(it, State.WHITE) }
        println("starting")
        return generateSequence(inProgressGrid.toMap()) { it.step() }
                .map { it.values.count { it == State.BLACK } }
                .withIndex()
                .onEach { println(it) }
                .map { it.value }
                .drop(100)
                .first()
    }

    private fun HexCoordinate.reduce() = reduceCache.computeIfAbsent(this) { it.reduceRaw() }

    private fun HexCoordinate.reduceRaw() =
            generateSequence(this) { iterate() }.windowed(2).first { (a, b) -> a == b }.first()

    private fun HexCoordinate.shortcut(direction1: String, direction2: String, resolved: String): HexCoordinate {
        val resolvedAdditions = Integer.min(this[direction1] ?: 0, this[direction2] ?: 0)
        return listOf(direction1, direction2).fold(this) { state, next ->
            state + (next to (state[next] ?: 0) - resolvedAdditions)
        }.let { it + (resolved to (it[resolved] ?: 0) + resolvedAdditions) }
    }

    private fun HexCoordinate.opposite(direction1: String, direction2: String): HexCoordinate {
        val changes = Integer.min(this[direction1] ?: 0, this[direction2] ?: 0)
        return listOf(direction1, direction2).fold(this) { state, next ->
            state + (next to (state[next] ?: 0) - changes)
        }
    }
}

fun main() {
    println(Day24.testInput.part1())
    println(Day24.input.part2())
}

