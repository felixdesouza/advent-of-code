package aoc2020

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class Day6Test {
    val testInput = """
        abc

        a
        b
        c

        ab
        ac

        a
        a
        a
        a

        b
    """.trimIndent()

    @Test
    internal fun `part 1 test input`() {
        assertEquals(11, Day6.part1(Day6.parseInput(testInput)))
    }

    @Test
    internal fun `part 1 real input`() {
        assertEquals(6585, Day6.part1(Day6.parseInput(Day6.input)))
    }

    @Test
    internal fun `part 2 test input`() {
        assertEquals(6, Day6.part2(Day6.parseInput(testInput)))
    }

    @Test
    internal fun `part 2 real input`() {
        assertEquals(3276, Day6.part2(Day6.parseInput(Day6.input)))
    }
}