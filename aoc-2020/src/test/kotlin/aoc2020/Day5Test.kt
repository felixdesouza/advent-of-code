package aoc2020

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class Day5Test {
    @Test
    internal fun `part 1 test input`() {
        assertEquals(820, Day5.part1(setOf("BFFFBBFRRR", "FFFBBBFRRR", "BBFFBBFRLL")))
    }

    @Test
    internal fun `part 1 real input`() {
        Day5.part1(Day5.input).let { println(it) }
    }

    @Test
    internal fun `part 2`() {
        assertEquals(569, Day5.part2(Day5.input))
    }
}