package aoc2021

import common.Coordinate
import common.readLines
import java.lang.Math.abs

object Day5 {
    fun parseInput(lines: List<String>): List<Pair<Coordinate, Coordinate>> {
        return lines.map {
            val (left, right) = it.split(" -> ").map { it.split(",").map { Integer.parseInt(it) } }
            val (leftX, leftY) = left
            val (rightX, rightY) = right

            Coordinate(leftX, leftY) to Coordinate(rightX, rightY)
        }
    }

    val input = readLines("/aoc2021/day5.txt")
            .let { parseInput(it) }

    val testInput = """
            0,9 -> 5,9
            8,0 -> 0,8
            9,4 -> 3,4
            2,2 -> 2,1
            7,0 -> 7,4
            6,4 -> 2,0
            0,9 -> 2,9
            3,4 -> 1,4
            0,0 -> 8,8
            5,5 -> 8,2
        """.trimIndent().lines()
            .let { parseInput(it) }

    fun part1(input: List<Pair<Coordinate, Coordinate>>): Int {
        val input = input.filter { (left, right) -> left.x == right.x || left.y == right.y }
        return countOverlappingLines(input)
    }

    private fun countOverlappingLines(input: List<Pair<Coordinate, Coordinate>>) =
            input.flatMap { (left, right) -> left.lineSegment(right) }.groupingBy { it }.eachCount()
                    .filterValues { it >= 2 }
                    .count()

    private fun Coordinate.lineSegment(other: Coordinate): List<Coordinate> {
        if (this.x == other.x) {
            return if (this.y < other.y) {
                (this.y..other.y).map { Coordinate(x, it) }
            } else {
                (this.y downTo other.y).map{ Coordinate(x, it) }
            }
        }

        if (this.y == other.y) {
            return if (this.x < other.x) {
                (this.x..other.x).map { Coordinate(it, y) }
            } else {
                (this.x downTo other.x).map{ Coordinate(it, y) }
            }
        }

        if (abs(this.x - other.x) != abs(this.y - other.y)) {
            throw AssertionError("malformed line")
        }

        return if (this.x < other.x) {
            if (this.y < other.y) {
                (this.x..other.x).map { Coordinate(it, this.y + (it - this.x)) }
            } else {
                (this.x..other.x).map { Coordinate(it, this.y - (it - this.x)) }
            }
        } else {
            other.lineSegment(this)
        }
    }

    fun part2(input: List<Pair<Coordinate, Coordinate>>): Int {
        return countOverlappingLines(input)
    }

    @JvmStatic
    fun main(args: Array<String>) {
        println(part1(testInput))
        println(part1(input))
        println(part2(testInput))
        println(part2(input))
    }
}
