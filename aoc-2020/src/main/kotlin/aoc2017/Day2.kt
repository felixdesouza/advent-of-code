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
}

fun main() {
    println(Day2.part1(Day2.input))
}