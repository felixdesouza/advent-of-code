package aoc2020

import common.readLines

object Day5 {
    val input = readLines("/aoc2020/day5.txt").toSet()

    fun part1(input: Set<String>): Int {
        return input.associateWith { parseIntoNumber(it) }
            .values
            .max()!!
    }

    private fun parseIntoNumber(passportString: String): Int {
        return passportString.map {
            when (it) {
                'F', 'L' -> 0
                'B', 'R' -> 1
                else -> throw AssertionError("invalid")
            }
        }.joinToString(separator = "")
            .let { Integer.valueOf(it, 2) }
    }

    fun part2(passportNumbers: Set<String>): Int {
        val sorted = input.map { parseIntoNumber(it) }.sorted()
        var l = 0
        var r = sorted.size - 1

        while (l < r) {
            val m = (l + r) / 2
            println("${sorted[m]} ${sorted[0]} ${sorted[0] + m} ${sorted[l]} ${sorted[r]}")
            if (sorted[m] == sorted[0] + m) {
                l = m + 1
            } else {
                r = m - 1
            }
        }

        return sorted[l] - 1
    }
}