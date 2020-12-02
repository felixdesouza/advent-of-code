package aoc2020

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class Day1KtTest {
    companion object {
        val testInput = setOf(1721, 979, 366, 299, 675, 1456L)
    }

    @Test
    internal fun part1_testInput() {
        assertEquals(514579, Day1.part1(testInput))
    }

    @Test
    internal fun part1() {
        assertEquals(858496, Day1.part1(Day1.input))
    }

    @Test
    internal fun part2_testInput() {
        assertEquals(241861950, Day1.part2(testInput))
    }

    @Test
    internal fun part2() {
        assertEquals(263819430, Day1.part2(Day1.input))
    }
}