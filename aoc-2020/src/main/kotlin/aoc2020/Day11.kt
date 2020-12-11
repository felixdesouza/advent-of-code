package aoc2020

import common.Coordinate
import common.Coordinate.Companion.origin
import common.readLines

object Day11 {
    val input  = readLines("/aoc2020/day11.txt").let { parseInput(it) }

    val testInput = """
        L.LL.LL.LL
        LLLLLLL.LL
        L.L.L..L..
        LLLL.LL.LL
        L.LL.LL.LL
        L.LLLLL.LL
        ..L.L.....
        LLLLLLLLLL
        L.LLLLLL.L
        L.LLLLL.LL
    """.trimIndent().lines().let { parseInput(it) }

    fun parseInput(lines: List<String>): Pair<Pair<Int, Int>, Map<Pair<Int, Int>, Char>> {
        val numRows = lines.size
        val numColumns = lines.first().length
        val grid = lines.withIndex()
            .map { (row, text) -> text.withIndex().map { (col, char) -> (row to col) to char } }
            .flatten()
            .toMap()
        return (numRows to numColumns) to grid
    }

    fun iterate(dimensions: Pair<Int, Int>, grid: Map<Pair<Int, Int>, Char>): Map<Pair<Int, Int>, Char> {
        val map = mutableMapOf<Pair<Int, Int>, Char>()
        val (rows, cols) = dimensions
        for (x in (0 until rows)) {
            for (y in (0 until cols)) {
                val current = grid[(x to y)]!!
                val new = when {
                    current == 'L' && adjacent(x, y, grid) == 0 -> '#'
                    current == '#' && adjacent(x, y, grid) >= 4 -> 'L'
                    else -> current
                }

                map[(x to y)] = new
            }
        }

        return map
    }

    fun iterate2(dimensions: Pair<Int, Int>, grid: Map<Pair<Int, Int>, Char>): Map<Pair<Int, Int>, Char> {
        val map = mutableMapOf<Pair<Int, Int>, Char>()
        val (rows, cols) = dimensions
        for (x in (0 until rows)) {
            for (y in (0 until cols)) {
                val current = grid[(x to y)]!!
                val new = when {
                    current == 'L' && adjacent2(x, y, grid) == 0 -> '#'
                    current == '#' && adjacent2(x, y, grid) >= 5 -> 'L'
                    else -> current
                }

                map[(x to y)] = new
            }
        }

        return map
    }

    fun part1(dimensions: Pair<Int, Int>, grid: Map<Pair<Int, Int>, Char>): Pair<Int, Int> {
        return compute(dimensions, grid, this::iterate)
    }

    fun part2(dimensions: Pair<Int, Int>, grid: Map<Pair<Int, Int>, Char>): Pair<Int, Int> {
        return compute(dimensions, grid, this::iterate2)
    }

    fun compute(
        dimensions: Pair<Int, Int>,
        grid: Map<Pair<Int, Int>, Char>,
        f: (dims: Pair<Int, Int>, grid: Map<Pair<Int, Int>, Char>) -> Map<Pair<Int, Int>, Char>
    ): Pair<Int, Int> {
        val stable = generateSequence(grid) { prev -> f(dimensions, prev) }
            .windowed(2)
            .withIndex()
            .first { (_, pair) ->
                val (prev, curr) = pair
                prev == curr
            }

        return (stable.index to stable.value.first().values.count { it == '#' })
    }

    fun adjacent(x: Int, y: Int, grid: Map<Pair<Int, Int>, Char>): Int {
        val coord = Coordinate(x, y)
        val neighbours = coord.neighbours() + coord.diagonalNeighbours()
        return neighbours.map { (nX, nY) -> nX to nY }
            .mapNotNull { grid[it] }
            .count { it == '#' }
    }

    val vectors = origin.allNeighbours()
    val chars = setOf('L', '#')

    fun adjacent2(x: Int, y: Int, grid: Map<Pair<Int, Int>, Char>): Int {
        val coord = Coordinate(x, y)
        return vectors.count { vector ->
            generateSequence(coord.plus(vector)) { curr -> curr.plus(vector) }
                .takeWhile { grid[it.x to it.y] != null }
                .firstOrNull { grid[it.x to it.y] in chars }
                ?.let { grid[it.x to it.y] == '#' } ?: false
        }
    }
}