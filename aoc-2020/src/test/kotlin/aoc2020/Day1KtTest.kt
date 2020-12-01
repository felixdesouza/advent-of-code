package aoc2020

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class Day1KtTest {
    @Test
    internal fun testInput() {
        assertEquals(514579, Day1.part1(setOf(1721, 979, 366, 299, 675, 1456)))
    }

    @Test
    internal fun part1() {
        assertEquals(858496, Day1.part1(Day1.input))
    }
}