package aoc2020

import aoc2020.Day2.PasswordWithPolicy
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class Day2Test {
    @Test
    internal fun `part1 test input`() {
        val input = setOf(
            PasswordWithPolicy((1..3), 'a', "abcde"),
            PasswordWithPolicy((1..3), 'b', "cdefg"),
            PasswordWithPolicy((2..9), 'c', "ccccccccc"))

        assertEquals(2, Day2.part1(input))
    }

    @Test
    internal fun `part1 real input`() {
        assertEquals(477, Day2.part1(Day2.input))
    }
}