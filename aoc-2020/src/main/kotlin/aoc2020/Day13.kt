package aoc2020

import common.readLines

object Day13 {
    val input = readLines("/aoc2020/day13.txt").let { parseInput(it) }

    fun parseInput(lines: List<String>): Pair<Int, List<Int>> {
        val (first, next) = lines

        return first.toInt() to next.split(",").filter { it != "x" }.map { it.toInt() }
    }

    fun part1(input: Pair<Int, List<Int>>): Long {
        val (start, numbers) = input

        val (id, wait) = numbers.associateWith { -1 * ((start % it) - it) }
            .entries
            .sortedBy { it.value }
            .onEach { println(it) }
            .first()!!

        return id*wait.toLong()
    }
}

fun main() {
    val testInput = """
        939
        7,13,x,x,59,x,31,19
    """.trimIndent().lines().let { Day13.parseInput(it) }.let { println(Day13.part1(it)) }

    println(Day13.part1(Day13.input))
}