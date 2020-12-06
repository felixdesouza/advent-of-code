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
    internal fun name() {
        assertEquals(11, Day6.part1(Day6.parseInput(testInput)))
    }

    @Test
    internal fun `part 1 real input`() {
        println(Day6.part1(Day6.parseInput(Day6.input)))
    }
}