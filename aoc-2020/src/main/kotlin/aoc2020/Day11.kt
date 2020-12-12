package aoc2020

import common.Coordinate
import common.Coordinate.Companion.origin
import common.Grid
import common.openFile

object Day11 {
    val input = Grid.parse(openFile("/aoc2020/day11.txt"), this::parsePosition)

    private fun parsePosition(it: Char) = when (it) {
        '.' -> Position.Floor
        '#' -> Position.Occupied
        'L' -> Position.Empty
        else -> throw AssertionError("invalid")
    }

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
    """.trimIndent().let { Grid.parse(it, this::parsePosition) }

    enum class Position {
        Floor, Empty, Occupied
    }

    fun part1(grid: Grid<Position>): Pair<Int, Int> {
        fun adjacent(grid: Grid<Position>, coordinate: Coordinate): Int {
            return coordinate.allNeighbours()
                .mapNotNull { grid[it] }
                .count { it == Position.Occupied }
        }

        return compute(grid) {
            it.mapGrid { coordinate, current ->
                when {
                    current == Position.Empty && adjacent(it, coordinate) == 0 -> Position.Occupied
                    current == Position.Occupied && adjacent(it, coordinate) >= 4 -> Position.Empty
                    else -> current
                }
            }
        }
    }

    fun part2(grid: Grid<Position>): Pair<Int, Int> {
        val vectors = origin.allNeighbours()
        val validPositions = setOf(Position.Empty, Position.Occupied)

        fun adjacent(grid: Grid<Position>, coordinate: Coordinate): Int {
            return vectors.count { vector ->
                generateSequence(coordinate.plus(vector)) { curr -> curr.plus(vector) }
                    .takeWhile { grid[it] != null }
                    .firstOrNull { grid[it] in validPositions }
                    ?.let { grid[it] == Position.Occupied } ?: false
            }
        }

        return compute(grid) {
            it.mapGrid { coordinate, current ->
                when {
                    current == Position.Empty && adjacent(it, coordinate) == 0 -> Position.Occupied
                    current == Position.Occupied && adjacent(it, coordinate) >= 5 -> Position.Empty
                    else -> current
                }
            }
        }
    }

    fun compute(
        grid: Grid<Position>,
        f: (Grid<Position>) -> Grid<Position>
    ): Pair<Int, Int> {
        val stable = generateSequence(grid) { prev -> f(prev) }
            .windowed(2)
            .withIndex()
            .first { (_, pair) ->
                val (prev, curr) = pair
                prev == curr
            }

        return (stable.index to stable.value.first().grid.values.count { it == Position.Occupied })
    }

}
