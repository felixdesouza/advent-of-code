package aoc2020

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class Day8Test {

    val testInput = """
        nop +0
        acc +1
        jmp +4
        acc +3
        jmp -3
        acc -99
        acc +1
        jmp -4
        acc +6
    """.trimIndent().lines().map { Day8.parseLine(it) }

    @Test
    internal fun `part 1 test input`() {
        assertEquals(5, Day8.part1(testInput))
    }

    @Test
    internal fun `part 1 real input`() {
        println(Day8.part1(Day8.input))
    }
}