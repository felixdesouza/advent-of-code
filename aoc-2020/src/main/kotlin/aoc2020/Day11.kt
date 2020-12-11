package aoc2020

import common.Coordinate
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
        println("---")
        for (x in (0 until rows)) {
            for (y in (0 until cols)) {
                val current = grid[(x to y)]!!
                val new = when {
                    current == 'L' && adjacent2(x, y, grid) == 0 -> '#'
                    current == '#' && adjacent2(x, y, grid) >= 5 -> 'L'
                    else -> current
                }
                //println("$x,$y: $current -> $new")
//                print(new)
                map[(x to y)] = new
            }
//            println()
        }

        return map
    }

    fun part1(dimensions: Pair<Int, Int>, grid: Map<Pair<Int, Int>, Char>): Pair<Int, Int> {
        val stable = generateSequence(grid) { prev -> iterate(dimensions, prev) }
            .windowed(2)
            .withIndex()
            .first { (_, pair) ->
                val (prev, curr) = pair
                prev == curr
            }

        return (stable.index to stable.value.first().values.count { it == '#' })
    }

    fun part2(dimensions: Pair<Int, Int>, grid: Map<Pair<Int, Int>, Char>): Pair<Int, Int> {
        val stable = generateSequence(grid) { prev -> iterate2(dimensions, prev) }
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

    val origin = Coordinate(0, 0)
    val vectors = origin.diagonalNeighbours().plus(origin.neighbours())
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

    fun Coordinate.plus(coordinate: Coordinate): Coordinate {
        return Coordinate(this.x + coordinate.x, this.y + coordinate.y)
    }
}

fun main() {
    val (dim, grid) = Day11.input
    println(Day11.part1(dim, grid))

    val (testDim, testGrid) = Day11.testInput

    println(Day11.part2(testDim, testGrid))
    println(Day11.part2(dim, grid))
}