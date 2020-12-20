package aoc2017

import kotlin.math.abs

object Day3 {

    fun part1(n: Int): Int {
        val sqrt = Math.sqrt(n.toDouble())
        val lower = Math.floor(sqrt).toInt()
        val upper = Math.ceil(sqrt).toInt()

        if (lower == upper) TODO("handle later")

        val lowerBound = if (lower % 2 == 0) lower - 1 else lower
        val upperBound = if (upper % 2 == 0) upper + 1 else upper

        val sizeOfQuadrant = (upperBound * upperBound - lowerBound * lowerBound) / 4
        val normalised = n - (lowerBound * lowerBound + 1)

        val level = upperBound / 2

        val position = abs((normalised % sizeOfQuadrant) - ((sizeOfQuadrant / 2) - 1))

        return level + position
    }
}

fun main() {
    println(Day3.part1(312051))
}