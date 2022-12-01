package aoc2022

import common.openFile

object Day1 {
    val input = openFile("/aoc2022/day1.txt").let { parseInput(it) }
    val testInput = """
        1000
        2000
        3000

        4000

        5000
        6000

        7000
        8000
        9000

        10000
    """.trimIndent().let { parseInput(it) }

    private fun parseInput(rawInput: String): List<List<Int>> {
        return rawInput.split("\n\n").map { it.lines().map { Integer.parseInt(it) } }
    }

    fun part1(numbers: List<List<Int>>): Int {
        return numbers.map { it.sum() }.max()!!
    }

    fun part2(numbers: List<List<Int>>): Int {
        return numbers.map { it.sum() }.sortedDescending().take(3).sum()
    }

    @JvmStatic
    fun main(args: Array<String>) {
        println(part1(input))
        println(part2(input))
    }
}

