package aoc2020

import aoc2020.Day24.part1
import common.readLines

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

    fun String.parseLine(): Map<String, Int> {
        fun split(accumulator: List<String>, line: String): List<String> {
            println("$accumulator | $line")
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

    fun Map<String, Int>.iterate() = shortcut("ne", "se", "e") // done
            .shortcut("nw", "sw", "w") // done
            .opposite("ne", "sw") // done
            .opposite("nw", "se") // done
            .opposite("e", "w") // done
            .shortcut("se", "w", "sw")
            .shortcut("ne", "w", "nw")
            .shortcut("sw", "e", "se")
            .shortcut("nw", "e", "ne")

    fun List<Map<String, Int>>.part1(): Int {
        return groupingBy { it }.eachCount().entries.count { it.value % 2 == 1 }
    }

    private fun Map<String, Int>.reduce() = generateSequence(this) { iterate() }.windowed(2).first { (a, b) -> a == b}.first()

    private fun Map<String, Int>.shortcut(direction1: String, direction2: String, resolved: String): Map<String, Int> {
        val resolvedAdditions = Integer.min(this[direction1] ?: 0, this[direction2] ?: 0)
        return listOf(direction1, direction2).fold(this) { state, next ->
            state + (next to (state[next] ?: 0) - resolvedAdditions)
        }.let { it + (resolved to (it[resolved] ?: 0) + resolvedAdditions) }
    }

    private fun Map<String, Int>.opposite(direction1: String, direction2: String): Map<String, Int> {
        val changes = Integer.min(this[direction1] ?: 0, this[direction2] ?: 0)
        return listOf(direction1, direction2).fold(this) { state, next ->
            state + (next to (state[next] ?: 0) - changes)
        }
    }
}

fun main() {
    println(Day24.input.part1())
}

