package aoc2020

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class Day9Test {
    val testInput = """
        35
        20
        15
        25
        47
        40
        62
        55
        65
        95
        102
        117
        150
        182
        127
        219
        299
        277
        309
        576
    """.trimIndent().lines().let { Day9.parseInput(it) }

    @Test
    internal fun `part 1 test input`() {
        assertEquals(127, Day9.part1(testInput, 5))
    }

    @Test
    internal fun `part 1 real input`() {
        assertEquals(257342611, Day9.part1(Day9.input, 25))
    }

    @Test
    internal fun `part 2 test input`() {
        assertEquals(62, Day9.part2(testInput, 127))
    }

    @Test
    internal fun `part 2 real input`() {
        assertEquals(35602097, Day9.part2(Day9.input, 257342611))
    }
}