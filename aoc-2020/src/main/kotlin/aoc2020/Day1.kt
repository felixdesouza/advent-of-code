package aoc2020

import common.readLines

object Day1 {
    val input = readLines("/aoc2020/day1.txt").map { it.toInt() }.toSet()
    fun part1(numbers: Set<Int>): Int {
        return numbers.find { (2020 - it) in numbers }!!.let { it * (2020 - it) }
    }
}
