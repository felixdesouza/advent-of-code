package sample

import sample.Day2.parseInput
import sample.Day2.part1
import kotlin.test.Test
import kotlin.test.assertEquals

internal class Day2Test {

    @Test
    fun part1() {
        assertEquals(3500, part1(parseInput("1,9,10,3,2,3,11,0,99,30,40,50")))
        assertEquals(2, part1(parseInput("1,0,0,0,99")))
        assertEquals(2, part1(parseInput("2,3,0,3,99")))
        assertEquals(2, part1(parseInput("2,4,4,5,99,0")))
        assertEquals(30, part1(parseInput("1,1,1,4,99,5,6,0,99")))
    }
}