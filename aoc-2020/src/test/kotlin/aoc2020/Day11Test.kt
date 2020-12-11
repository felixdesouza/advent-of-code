package aoc2020

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class Day11Test {
    @Test
    internal fun `part 1 real input`() {
        assertEquals(2441, Day11.part1(Day11.input).second)
    }

    @Test
    internal fun `part 2 test input`() {
        assertEquals(26, Day11.part2(Day11.testInput).second)
    }

    @Test
    internal fun `part 2 real input`() {
        assertEquals(2190, Day11.part2(Day11.input).second)
    }
}