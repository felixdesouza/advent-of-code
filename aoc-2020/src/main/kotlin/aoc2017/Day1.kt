package aoc2017

import common.openFile

object Day1 {
    val input = openFile("/aoc2017/day1.txt").toList()

    fun part1(input: List<Char>): Int {
        return compute(input, 1)
    }

    fun part2(input: List<Char>): Int {
        return compute(input, input.size / 2)
    }

    private fun compute(input: List<Char>, step: Int): Int {
        return input.withIndex().filter { (index, value) -> input[(index + step) % input.size] == value }
                .map { it.value - '0' }
                .sum()
    }

}

fun main() {
    println(Day1.part1(Day1.input))
    println(Day1.part2(Day1.input))
}