package aoc2017

import common.readLines

object Day4 {
    val input = readLines("/aoc2017/day4.txt").map { parseInput(it) }

    fun parseInput(line: String) = line.split(" ")

    fun part1(input: List<List<String>>) = input.count { it.toSet().size == it.size }
}

fun main() {
    println(Day4.part1(Day4.input))
}

