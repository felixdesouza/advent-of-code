package aoc2021

import com.google.common.graph.ValueGraph
import com.google.common.graph.ValueGraphBuilder
import common.Coordinate
import common.Grid
import common.dijkstra
import common.openFile

object Day15 {

    val input = openFile("/aoc2021/day15.txt").let { Input.parse(it) }

    val testInput = """
        1163751742
        1381373672
        2136511328
        3694931569
        7463417111
        1319128137
        1359912421
        3125421639
        1293138521
        2311944581
    """.trimIndent()
            .let { Input.parse(it) }

    data class Input(val grid: Grid<Int>, val graph: ValueGraph<Coordinate, Int>) {
        companion object {
            fun parse(input: String): Input {
                val grid = Grid.parse(input) { it - '0' }
                val graph = parseGraph(grid)

                return Input(grid, graph)
            }

            fun parseGraph(grid: Grid<Int>): ValueGraph<Coordinate, Int> {
                val graph = ValueGraphBuilder.directed()
                        .build<Coordinate, Int>()
                grid.fold(graph) { g, next, _ ->
                    val neighbours = next.neighbours().filter { grid[it] != null }

                    neighbours.forEach { neighbour -> g.putEdgeValue(next, neighbour, grid[neighbour]!!) }
                    g
                }

                return graph
            }
        }

        fun enlarge(): Input {
            val numRows = grid.numRows * 5
            val numColumns = grid.numColumns * 5

            val coordinates = (0 until numRows).flatMap { y -> (0 until numColumns).map { x -> Coordinate(x, y) } }
                    .associateWith { coordinate ->
                        val (x, y) = coordinate

                        val originalCoordinate = Coordinate(x % grid.numColumns,y % grid.numRows)
                        val originalValue = grid[originalCoordinate]!!

                        val xDepth = x / grid.numColumns
                        val yDepth = y / grid.numRows

                        (originalValue + xDepth + yDepth).let { if (it >= 10) (it - 9) else it }
                    }

            val newGrid = Grid(coordinates.toMutableMap(), numRows, numColumns)
            val newGraph = parseGraph(newGrid)
            return Input(newGrid, newGraph)
        }
    }

    fun part1(input: Input): Long {
        val dijkstra = dijkstra(input.graph, Coordinate.origin, false)
        val end = Coordinate(input.grid.numColumns - 1, input.grid.numRows - 1)
        return dijkstra[end]!!.toLong()
    }

    fun part2(input: Input): Long {
        val enlarged = input.enlarge()
        return part1(enlarged)
    }

    @JvmStatic
    fun main(args: Array<String>) {
        println(part1(testInput))
        println(part1(input))
        println(part2(testInput))
        println(part2(input))
    }

}
