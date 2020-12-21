package aoc2017

import common.Coordinate
import kotlin.math.abs

object Day3 {

    fun part1(n: Int): Int {
        val (x, y)  = coordinate(n)
        return abs(x) + abs(y)
    }

    fun coordinate(i: Int): Coordinate {
        val sqrt = Math.sqrt(i.toDouble())
        val lower = Math.floor(sqrt).toInt()
        val upper = Math.ceil(sqrt).toInt()

        if (lower == upper && lower % 2 == 1) return Coordinate(lower / 2, lower / 2)

        val lowerBound = if (lower % 2 == 0) lower - 1 else lower
        val upperBound = if (upper % 2 == 0) upper + 1 else upper

        val level = upperBound / 2
        val sizeOfQuadrant = (upperBound * upperBound - lowerBound * lowerBound) / 4
        val normalised = i - (lowerBound * lowerBound + 1)
        val position = normalised % sizeOfQuadrant
        val quadrant = normalised / sizeOfQuadrant

        val upperBoundCoordinate = Coordinate(level, level)

        return when (quadrant) {
            0 -> upperBoundCoordinate + Coordinate(0, -(1 + position))
            1 -> upperBoundCoordinate + Coordinate(-(1 + position), -sizeOfQuadrant)
            2 -> upperBoundCoordinate + Coordinate(-sizeOfQuadrant, position + 1 - sizeOfQuadrant)
            3 -> upperBoundCoordinate + Coordinate(-sizeOfQuadrant + 1 + position, 0)
            else -> throw AssertionError("not expected $quadrant")
        }
    }

    fun part2(upperBound: Int): Long {
        generateSequence(2) { it + 1 }
                .fold(mapOf(Coordinate.origin to 1L)) { grid, next ->
                    val coordinate = coordinate(next)
                    val value = coordinate.allNeighbours().map { grid.getOrDefault(it, 0L) }.sum()
                    if (value > upperBound) return value
                    grid + (coordinate to value)
                }

        throw AssertionError("not found")
    }
}

fun main() {
    println(Day3.part1(312051))
    println(Day3.part2(312051))
}