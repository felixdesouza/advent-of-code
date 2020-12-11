package aoc2020

import common.Coordinate
import common.Coordinate.Companion.origin
import common.Grid
import common.openFile

object Day11 {
    val input  = Grid.parse(openFile("/aoc2020/day11.txt"))

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
    """.trimIndent().let { Grid.parse(it) }

    fun part1(grid: Grid<Char>): Pair<Int, Int> {
        fun adjacent(grid: Grid<Char>, coordinate: Coordinate): Int {
            val neighbours = coordinate.allNeighbours()
            return neighbours
                .mapNotNull { grid[it] }
                .count { it == '#' }
        }

        return compute(grid) {
            it.mapGrid { coordinate, current ->
                when {
                    current == 'L' && adjacent(it, coordinate) == 0 -> '#'
                    current == '#' && adjacent(it, coordinate) >= 4 -> 'L'
                    else -> current
                }
            }
        }
    }

    fun part2(grid: Grid<Char>): Pair<Int, Int> {
        val vectors = origin.allNeighbours()
        val chars = setOf('L', '#')

        fun adjacent(grid: Grid<Char>, coordinate: Coordinate): Int {
            return vectors.count { vector ->
                generateSequence(coordinate.plus(vector)) { curr -> curr.plus(vector) }
                    .takeWhile { grid[it] != null }
                    .firstOrNull { grid[it] in chars }
                    ?.let { grid[it] == '#' } ?: false
            }
        }

        return compute(grid) {
            it.mapGrid { coordinate, current ->
                when {
                    current == 'L' && adjacent(it, coordinate) == 0 -> '#'
                    current == '#' && adjacent(it, coordinate) >= 5 -> 'L'
                    else -> current
                }
            }
        }
    }

    fun compute(
        grid: Grid<Char>,
        f: (Grid<Char>) -> Grid<Char>
    ): Pair<Int, Int> {
        val stable = generateSequence(grid) { prev -> f(prev) }
            .windowed(2)
            .withIndex()
            .first { (_, pair) ->
                val (prev, curr) = pair
                prev == curr
            }

        return (stable.index to stable.value.first().grid.values.count { it == '#' })
    }

}
