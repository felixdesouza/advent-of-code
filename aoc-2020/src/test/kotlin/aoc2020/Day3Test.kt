package aoc2020

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class Day3Test {
    val testInput = """
        ..##.......
        #...#...#..
        .#....#..#.
        ..#.#...#.#
        .#...##..#.
        ..#.##.....
        .#.#.#....#
        .#........#
        #.##...#...
        #...##....#
        .#..#...#.#
    """.trimIndent().lines().let { Day3.parseInput(it) }

    @Test
    internal fun `part 1 test input`() {
        assertEquals(7, Day3.part1(testInput))
    }

    @Test
    internal fun `part 2 real input`() {
        assertEquals(232, Day3.part1(Day3.input))
    }
}