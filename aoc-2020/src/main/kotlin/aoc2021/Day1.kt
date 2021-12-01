package aoc2021

import common.readLines

object Day1 {
    val input = readLines("/aoc2021/day1.txt").map { it.toInt() }
    val testInput = listOf(199, 200, 208, 210, 200, 207, 240, 269, 260, 263)

    fun part1(numbers: List<Int>): Int {
        return numbers.windowed(2).count { (a, b) -> b > a }
    }

    fun part2(numbers: List<Int>): Int {
        val modifiedInput = numbers.windowed(3).map { it.sum() }
        return part1(modifiedInput)
    }

    @JvmStatic
    fun main(args: Array<String>) {
        part1(input).also { println(it) }
        part2(input).also { println(it) }

        part2(testInput).also { println(it) }
    }
}

