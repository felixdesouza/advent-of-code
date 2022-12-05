package aoc2022

import common.Problem

object Day4 : Problem() {
    val input = rawInput.let { parseInput(it) }
    val testInput = """
        2-4,6-8
        2-3,4-5
        5-7,7-9
        2-8,3-7
        6-6,4-6
        2-6,4-8
    """.trimIndent().let { parseInput(it) }

    private fun parseInput(rawInput: String): List<Pair<IntRange, IntRange>> = rawInput.lines()
            .map {
                val (first, second) = it.split(",").map {
                    val (start, end) = it.split("-").map { it.toInt() }
                    start..end
                }
                first to second
            }

    fun part1(lines: List<Pair<IntRange, IntRange>>): Int {
        return lines.count { (firstElf, secondElf) ->
            (secondElf.first in firstElf && secondElf.last in firstElf) ||
                    (firstElf.first in secondElf && firstElf.last in secondElf)
        }
    }


    fun part2(lines: List<Pair<IntRange, IntRange>>): Int {
        return lines.count { (firstElf, secondElf) ->
            secondElf.first in firstElf || secondElf.last in firstElf ||
                    firstElf.first in secondElf || firstElf.last in secondElf
        }
    }

    @JvmStatic
    fun main(args: Array<String>) {
        println(part1(input))
        println(part2(input))
    }
}

