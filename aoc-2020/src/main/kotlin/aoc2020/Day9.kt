package aoc2020

import common.readLines

object Day9 {
    val input = readLines("/aoc2020/day9.txt").let { parseInput(it) }

    fun parseInput(lines: List<String>) = lines.map { it.toLong() }

    fun part1(input: List<Long>, window: Int): Long {
        return input.asSequence().windowed(window + 1) { it }
            .first { preambleWithTarget ->
                val preamble = preambleWithTarget.subList(0, window)
                val target = preambleWithTarget[window]

                !Day1.findTarget(preamble, target)
            }
            .let { it[window] }
    }
}