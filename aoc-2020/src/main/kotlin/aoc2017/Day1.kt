package aoc2017

import common.openFile

object Day1 {
    val input = openFile("/aoc2017/day1.txt").toList()

    fun part1(input: List<Char>): Int {
        val complete = input + input.first()
        return complete.windowed(2).filter { (a, b) -> a == b }.map { it.first() }.map { it - '0' }.sum()
    }

}

fun main() {
    println(Day1.part1(Day1.input))
}