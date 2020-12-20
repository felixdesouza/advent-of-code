package aoc2017

import common.readLines

object Day2 {
    val input = readLines("/aoc2017/day2.txt").let { parseInput(it) }
    fun parseInput(input: List<String>): List<List<Int>> {
        return input.map { it.split("\\s+".toRegex()).map { it.toInt() } }
    }

    fun part1(input: List<List<Int>>): Int {
        return input.sumBy { line -> line.max()!! - line.min()!! }
    }

    fun check(line: List<Int>): Int {
        val sorted = line.sorted()
        return sorted.dropLast(1).asSequence().withIndex().mapNotNull { (index, source) ->
            val others = sorted.subList(index+1, sorted.size)
            others.firstOrNull { it % source == 0 }?.let { it / source }
        }.first()
    }

    fun part2(input: List<List<Int>>): Int {
        return input.sumBy { check(it) }
    }
}

fun main() {
    println(Day2.part1(Day2.input))
    val testInput = """
        5 9 2 8
        9 4 7 3
        3 8 6 5
    """.trimIndent().lines().let { Day2.parseInput(it) }
    println(Day2.part2(Day2.input))
}