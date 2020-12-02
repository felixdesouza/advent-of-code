package aoc2020

import common.readLines

object Day1 {
    val input = readLines("/aoc2020/day1.txt").map { it.toLong() }.toSet()
    fun part1(numbers: Set<Long>): Long {
        return doSomething(numbers, 2020)!!
    }

    private fun doSomething(numbers: Set<Long>, target: Long): Long? {
        return numbers.find { (target - it) in numbers }?.let { it * (target - it) }
    }

    fun part2(numbers: Set<Long>): Long {
        return numbers.associateWith { first -> doSomething(numbers, 2020 - first)?.let { it * first } }
            .filterValues { it != null }
            .mapValues { (_, i) -> i!! }
            .values.first()
    }
}
