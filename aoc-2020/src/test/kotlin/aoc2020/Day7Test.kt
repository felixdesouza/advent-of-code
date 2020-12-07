package aoc2020

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class Day7Test {
    val testInput = """
        light red bags contain 1 bright white bag, 2 muted yellow bags.
        dark orange bags contain 3 bright white bags, 4 muted yellow bags.
        bright white bags contain 1 shiny gold bag.
        muted yellow bags contain 2 shiny gold bags, 9 faded blue bags.
        shiny gold bags contain 1 dark olive bag, 2 vibrant plum bags.
        dark olive bags contain 3 faded blue bags, 4 dotted black bags.
        vibrant plum bags contain 5 faded blue bags, 6 dotted black bags.
        faded blue bags contain no other bags.
        dotted black bags contain no other bags.
    """.trimIndent().lines().let { Day7.parseInput(it) }

    val testInput2 = """
        shiny gold bags contain 2 dark red bags.
        dark red bags contain 2 dark orange bags.
        dark orange bags contain 2 dark yellow bags.
        dark yellow bags contain 2 dark green bags.
        dark green bags contain 2 dark blue bags.
        dark blue bags contain 2 dark violet bags.
        dark violet bags contain no other bags.
    """.trimIndent().lines().let { Day7.parseInput(it) }

    @Test
    internal fun `part 1 test input`() {
        assertEquals(4, Day7.part1(testInput))
    }

    @Test
    internal fun `part 1 real input`() {
        assertEquals(332, Day7.part1(Day7.input))
    }

    @Test
    internal fun `part 2 test input 1`() {
        assertEquals(32, Day7.part2(testInput))
    }

    @Test
    internal fun `part 2 test input 2`() {
        assertEquals(126, Day7.part2(testInput2))
    }

    @Test
    internal fun `part 2 real input`() {
        assertEquals(10875, Day7.part2(Day7.input))
    }
}