package aoc2022

import common.Problem

object Day3 : Problem() {
    val input = rawInput.let { parseInput(it) }
    val testInput = """
        vJrwpWtwJgWrhcsFMMfFFhFp
        jqHRNqRjqzjGDLGLrsFMfFZSrLrFZsSL
        PmmdzqPrVvPwwTWBwg
        wMqvLMZHhHMvwLHjbvcjnnSBnvTQFn
        ttgJtRGJQctTZtZT
        CrZsJsPPZsGzwwsLwLmpwMDw
    """.trimIndent().let { parseInput(it) }

    private fun parseInput(rawInput: String): List<String> = rawInput.lines()

    fun part1(lines: List<String>): Int = lines
            .map { it.subSequence(0, it.length / 2).toString() to it.subSequence(it.length / 2, it.length).toString() }
            .map { (first, second) -> first.toSet().intersect(second.toSet()).single() }
            .map { priority(it) }
            .sum()

    fun part2(pairs: List<String>): Int {
        return pairs.chunked(3)
                .map { it.map { it.toSet() }.reduce { a, b -> a.intersect(b) }.single() }
                .map { priority(it) }
                .sum()
    }

    private fun priority(type: Char): Int {
        return when (type) {
            in 'a'..'z' -> type - 'a' + 1
            in 'A'..'Z' -> type - 'A' + 27
            else -> throw AssertionError("not a valid type")
        }
    }

    @JvmStatic
    fun main(args: Array<String>) {
        println(part1(input))
        println(part2(input))
    }
}

