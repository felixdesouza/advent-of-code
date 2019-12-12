package sample

import readLines

object Day1 {
    private val input = readLines("sample/day1.txt").map(String::toInt)
    private fun fuel(mass: Int): Int = mass / 3 - 2

    fun part1(): Int {
        return input.map(::fuel).sum()
    }

    private fun recursiveFuel(mass: Int): Int {
        return generateSequence(fuel(mass)) {
            val next = fuel(it)
            if (next >= 0) next else null
        }.sum()
    }

    fun part2(): Int {
        return input.map(::recursiveFuel).sum()
    }

}

fun main() {
    println("day1 part1: ${Day1.part1()}")
    println("day1 part2: ${Day1.part2()}")
}

