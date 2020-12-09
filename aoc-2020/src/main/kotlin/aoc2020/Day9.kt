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

    fun part2(input: List<Long>, invalidNumber: Long): Long {
        var leftIndex = 0
        var rightIndex = 1
        var currentSum = input.subList(leftIndex, rightIndex).sum()

        while (rightIndex < input.size && leftIndex < rightIndex) {
            if (currentSum == invalidNumber) {
                val finalSublist = input.subList(leftIndex, rightIndex)
                return finalSublist.min()!! + finalSublist.max()!!
            }

            if (currentSum + input[rightIndex] > invalidNumber) {
                currentSum -= input[leftIndex]
                leftIndex += 1
            } else {
                currentSum += input[rightIndex]
                rightIndex += 1
            }
        }

        throw AssertionError("Not found")
    }
}