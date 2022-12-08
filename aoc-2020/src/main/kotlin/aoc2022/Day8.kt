package aoc2022

import common.Coordinate
import common.Grid
import common.Problem
import java.lang.Integer.min

object Day8 : Problem() {

    val input = rawInput.let { parse(it) }
    val testInput = """
        30373
        25512
        65332
        33549
        35390
    """.trimIndent().let { parse(it) }

    private fun parse(input: String): Grid<Int> = Grid.parse(input) { (it - '0') }

    fun part1(input: Grid<Int>): Int {
        return input.filter { coordinate, value ->
            coordinate.directionalNeighbours(input.numColumns, input.numRows)
                    .any { it.all { value > input[it]!! } }
        }.count()
    }

    fun part2(input: Grid<Int>): Int {
        fun checkDirection(value: Int, directionalNeighbours: List<Coordinate>): Int {
            return when {
                directionalNeighbours.isEmpty() -> 0
                else -> min(directionalNeighbours.size, directionalNeighbours.map { input[it]!! }.takeWhile { value > it }.count() + 1)
            }
        }
        return input.mapGrid { coordinate, value ->
            coordinate.directionalNeighbours(input.numColumns, input.numRows)
                    .map { checkDirection(value, it) }
                    .reduce { a, b -> a * b }
        }.grid.maxBy { it.value }.also { println(it) }.let { it!!.value }
    }

    private fun Coordinate.directionalNeighbours(numColumns: Int, numRows: Int): Set<List<Coordinate>> = setOf(leftNeighbours(), rightNeighbours(numColumns), topNeighbours(), bottomNeighbours(numRows))
    private fun Coordinate.leftNeighbours(): List<Coordinate> = (x - 1 downTo 0).map { Coordinate(it, y) }
    private fun Coordinate.rightNeighbours(limit: Int): List<Coordinate> = (x + 1 until limit).map { Coordinate(it, y) }
    private fun Coordinate.topNeighbours(): List<Coordinate> = (y - 1 downTo 0).map { Coordinate(x, it) }
    private fun Coordinate.bottomNeighbours(limit: Int): List<Coordinate> = (y + 1 until limit).map { Coordinate(x, it) }


    @JvmStatic
    fun main(args: Array<String>) {
        println(part1(input))
        println(part2(input))
    }
}

