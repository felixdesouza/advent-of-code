package aoc2020

import common.readLines

object Day13 {
    val input = readLines("/aoc2020/day13.txt").let { parseInput(it) }

    fun parseInput(lines: List<String>): Pair<Int, List<Pair<Int, Int>>> {
        val (first, next) = lines

        return first.toInt() to next.split(",").withIndex().filter { it.value != "x" }.map { (index, value) -> index to value.toInt() }
    }

    fun part1(input: Pair<Int, List<Pair<Int, Int>>>): Long {
        val (start, bla) = input
        val numbers = bla.map { it.second }

        val (id, wait) = numbers.associateWith { -1 * ((start % it) - it) }
            .entries
            .sortedBy { it.value }
            .onEach { println(it) }
            .first()!!

        return id*wait.toLong()
    }

    fun part2(input: Pair<Int, List<Pair<Int, Int>>>): Long {
        // chinese remainder theorem then plus/minus product until first answer

        input.second
            .map { (index, value) -> "x = ${value - index} mod $value " }
            .forEach { println(it) }

        return 0
    }

    fun solvePart2(input: Pair<Int, List<Pair<Int, Int>>>, solvedCrt: Long): Long {
        val n = input.second.map { it.second }.map { it.toLong() }.reduce { a, b -> a * b}
        return (solvedCrt % n + n) % n
    }
}

fun main() {
    val testInput = """
        939
        7,13,x,x,59,x,31,19
    """.trimIndent().lines().let { Day13.parseInput(it) }

    println(Day13.part1(Day13.input))
    println(Day13.part2(Day13.input))
    println(Day13.solvePart2(Day13.input, 267666633441334559))
}