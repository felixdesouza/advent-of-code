package aoc2017

import aoc2017.Day14.part1

object Day14 {
    val input = "stpzcrnm"
    val testInput = "flqrgnkx"
    fun String.part1(): Int {
        val input = (0..127).map { "$this-$it" }

        return input.map { Day10.part2(it) }
                .map { it.chunked(8).map { it.toLong(16).toString(2).padStart(32, '0') }.joinToString("") }
                .sumBy { it.count { it == '1' } }
    }
}

fun main() {
    Day14.input.part1().also { println(it) }
}