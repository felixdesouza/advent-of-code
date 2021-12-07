package aoc2021

import common.openFile
import java.lang.Math.abs

object Day7 {
    fun parseInput(input: String): List<Int> {
        return input.trim().split(",").map { Integer.parseInt(it) }
    }

    val input = openFile("/aoc2021/day7.txt")
            .let { parseInput(it) }

    val testInput = "16,1,2,0,4,2,7,1,2,14"
            .let { parseInput(it) }

    fun part1(input: List<Int>): Int {
        return input.map { calculateFuelSpend(input, it) }.min()!!
    }

    private fun calculateFuelSpend(input: List<Int>, position: Int): Int {
        return input.map { abs(it - position) }.sum()
    }

    private fun calculateFuelSpend2(input: List<Int>, position: Int): Int {
        return input.map {
            val n = abs(it - position)
            (n * (n + 1)) / 2
        }.sum()
    }

    fun part2(input: List<Int>): Int {
        val max = input.max()!!
        return (0..max).map { calculateFuelSpend2(input, it) }.min()!!
    }

    @JvmStatic
    fun main(args: Array<String>) {
        println(part1(testInput))
        println(part1(input))
        println(part2(testInput))
        println(part2(input))
    }
}
