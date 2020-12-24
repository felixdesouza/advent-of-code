package aoc2020

import aoc2020.Day24.part1
import aoc2020.Day24.part2
import common.readLines
import kotlin.math.abs

object Day24 {

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

        return split(emptyList(), this).fold(HexCoordinate.origin) { current, next -> current.next(next) }
    }

    fun List<HexCoordinate>.part1(): Int {
        return counts()
                .entries.count { it.value % 2 == 1 }
    }

    private fun List<HexCoordinate>.counts() = groupingBy { it }.eachCount()
    enum class State {
        WHITE, BLACK
    }

    fun List<HexCoordinate>.part2(): Int {
        val counts = counts().mapValues {
            when (it.value % 2) {
                0 -> State.WHITE
                1 -> State.BLACK
                else -> throw AssertionError("not possible $it")
            }
        }

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

        val fullGrid = generateSequence(setOf(HexCoordinate.origin)) { currentLevelCoordinates -> currentLevelCoordinates.nextLevelCoordinates() }
                .withIndex()
                .map { it.value }
                .take(20)
                .flatten()
                .fold(counts) { inProgressGrid, next -> inProgressGrid + (next to inProgressGrid.getOrDefault(next, State.WHITE)) }

        return generateSequence(fullGrid.toMap()) { it.step() }
                .map { it.values.count { it == State.BLACK } }
                .withIndex()
                .map { it.value }
                .drop(100)
                .first()
    }
}

data class HexCoordinate(val x: Int, val y: Int, val z: Int) {
    companion object {
        val origin = HexCoordinate(0, 0, 0)
        val directions = listOf("e", "w", "ne", "nw", "se", "sw")
    }

    fun next(instruction: String): HexCoordinate {
        return when(instruction) {
            "e" -> copy(x = x + 1, z = z - 1)
            "w" -> copy(x = x - 1, z = z + 1)
            "se" -> copy(x = x + 1, y = y - 1)
            "sw" -> copy(y = y - 1, z = z + 1)
            "ne" -> copy(y = y + 1, z = z - 1)
            "nw" -> copy(x = x - 1, y = y + 1)
            else -> throw AssertionError("unexpected")
        }
    }

    fun neighbours(): Set<HexCoordinate> = directions.map { this.next(it) }.toSet()

    fun steps() = listOf(x, y, z).map { abs(it) }.max()!!
}

fun main() {
    println(Day24.input.part1())
    println(Day24.input.part2())
}

