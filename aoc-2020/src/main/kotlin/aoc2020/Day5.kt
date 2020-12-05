package aoc2020

import common.readLines

object Day5 {
    val input = readLines("/aoc2020/day5.txt").toSet()

    fun part1(input: Set<String>): Int {
        return input.associateWith { parseIntoNumber(it) }
            .onEach { println(it) }
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
            .also { println(it) }
            .let { Integer.valueOf(it, 2) }
    }
}