package aoc2020

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class Day11Test {
    @Test
    internal fun `part 1 real input`() {
        val (dim, grid) = Day11.input
        assertEquals(2441, Day11.part1(dim, grid).second)
    }

    @Test
    internal fun `part 2 test input`() {
        val (dim, grid) = Day11.testInput
        assertEquals(26, Day11.part2(dim, grid).second)
    }

    @Test
    internal fun `part 2 real input`() {
        val (dim, grid) = Day11.input
        assertEquals(2190, Day11.part2(dim, grid).second)
    }
}