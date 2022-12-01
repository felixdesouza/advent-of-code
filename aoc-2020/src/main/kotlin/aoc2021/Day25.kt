package aoc2021

import common.Coordinate
import common.Grid
import common.openFile

object Day25 {

    val input = openFile("/aoc2021/day25.txt").parse()

    val testInput = """
        v...>>.vv>
        .vv>>.vv..
        >>.>v>...v
        >>v>>.>.v.
        v>v.vv.v..
        >.>>..v...
        .vv..>.>v.
        v.v..>>v.v
        ....v..v.>
    """.trimIndent().parse()

    val testInput2 = """
        ...>>>>>...
    """.trimIndent().parse()

    val testInput3 = """
        ..........
        .>v....v..
        .......>..
        ..........
    """.trimIndent().parse()

    val testInput4 = """
        ...>...
        .......
        ......>
        v.....>
        ......>
        .......
        ..vvv..
    """.trimIndent().parse()

    enum class Tile {
        SOUTH, EAST, EMPTY;

        override fun toString() = when (this) {
            SOUTH -> "v"
            EAST -> ">"
            EMPTY -> "."
        }
    }

    fun String.parse(): Map {
        val grid = Grid.parse(this) { char ->
            when (char) {
                '>' -> Tile.EAST
                'v' -> Tile.SOUTH
                else -> Tile.EMPTY
            }
        }

        val tiles = grid.filter { c, t -> t == Tile.EAST || t == Tile.SOUTH }
        val (eastFacing, southFacing) = tiles.toList().partition { it.second == Tile.EAST }

        return Map(southFacing.toMap().keys, eastFacing.toMap().keys, grid.numColumns, grid.numRows)
    }

    data class Map(val southFacing: Set<Coordinate>, val eastFacing: Set<Coordinate>, val x: Int, val y: Int) {
        fun step() = moveEast().moveSouth()

        fun moveEast(): Map {
            val toMove = eastFacing.associateWith {
                val eastNeighbour = it + Coordinate(1, 0)
                val correctedEastNeighbour = if (eastNeighbour.x == x) eastNeighbour.copy(x = 0) else eastNeighbour
                correctedEastNeighbour
            }.filterValues { eastNeighbour ->
                eastNeighbour !in southFacing && eastNeighbour !in eastFacing
            }

            return copy(eastFacing = eastFacing - toMove.keys + toMove.values)
        }

        fun moveSouth(): Map {
            val toMove = southFacing.associateWith {
                val southNeighbour = it + Coordinate(0, 1)
                val correctedSouthNeighbour = if (southNeighbour.y == y) southNeighbour.copy(y = 0) else southNeighbour
                correctedSouthNeighbour
            }.filterValues { southNeighbour ->
                southNeighbour !in southFacing && southNeighbour !in eastFacing
            }

            return copy(southFacing = southFacing - toMove.keys + toMove.values)
        }

        fun print() {
            for (y in 0 until y) {
                for (x in 0 until x) {
                    val coordinate = Coordinate(x, y)
                    val char = when (coordinate) {
                        in eastFacing -> '>'
                        in southFacing -> 'v'
                        else -> '.'
                    }
                    print(char)
                }
                println()
            }
            println()
        }
    }

    fun parts(input: Map) {
        val (index, map) = generateSequence(input) { it.step() }.windowed(2).withIndex().first { (_, v) ->
            val (a, b) = v
            a == b
        }

        println("part 1: ${index + 1}")
    }


    @JvmStatic
    fun main(args: Array<String>) {
        parts(input)
    }

}