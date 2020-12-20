package aoc2020

import common.Coordinate
import common.Grid
import common.openFile

object Day20 {

    val input = openFile("/aoc2020/day20.txt").let { parseInput(it) }

    data class Tile(val id: Int, val n: Int, val north: Int, val east: Int, val south: Int, val west: Int) {
        fun flips(): Set<Tile> {
            return setOf(copy(north = north.reverseInt(), east = west, west = east, south = south.reverseInt()),
                    copy(north = south, south = north, west = west.reverseInt(), east = east.reverseInt()),
                    copy(east = north.reverseInt(), north = east.reverseInt(), south = west.reverseInt(), west = south.reverseInt()),
                    copy(west = north, north = west, east = south, south = east))
        }

        fun rotate(): Tile {
            return copy(east = north, south = east.reverseInt(), west = south, north = west.reverseInt())
        }

        fun Int.reverseInt(): Int {
            return Integer.toBinaryString(this).padStart(n, '0').reversed().toInt(2)
        }

        fun orientations(): Set<Tile> {
            return generateSequence(this) { it.rotate() }.take(4).flatMap { (it.flips() + it).asSequence() }.toSet()
        }

        fun fitsWith(tile: Tile): Set<Direction> {
            fun matchingSide(tile: Tile): Direction? {
                return when {
                    this.north == tile.south -> Direction.N
                    this.south == tile.north -> Direction.S
                    this.east == tile.west -> Direction.E
                    this.west == tile.east -> Direction.W
                    else -> null
                }
            }

            return tile.orientations().mapNotNull { matchingSide(it) }.toSet()
        }
    }

    enum class Direction {
        N, S, E, W
    }

    fun parseInput(raw: String): Set<Tile> {
        return raw.split("\n\n").map { parseTile(it) }.toSet()
    }

    fun parseTile(raw: String): Tile {
        val id = raw.lines().first().replace("Tile ", "").replace(":", "").toInt()

        val grid = Grid.parse(raw.lines().drop(1).joinToString("\n")) {
            when (it) {
                '.' -> 0
                '#' -> 1
                else -> throw AssertionError("unexpected input")
            }
        }

        val north = (0 until grid.numColumns).map { grid[Coordinate(it, 0)] }.joinToString("").toInt(2)
        val east = (0 until grid.numRows).map { grid[Coordinate(grid.numColumns - 1, it)] }.joinToString("").toInt(2)
        val south = (0 until grid.numColumns).map { grid[Coordinate(it, grid.numRows - 1)] }.joinToString("").toInt(2)
        val west = (0 until grid.numColumns).map { grid[Coordinate(0, it)] }.joinToString("").toInt(2)

        return Tile(id, grid.numRows, north, east, south, west)
    }

    fun part1(input: Set<Tile>): Long {
        val validTiles = input.filter { tile ->
            val otherTiles = input - tile

            tile.orientations().any { oriented ->
                otherTiles.flatMap { otherTile -> oriented.fitsWith(otherTile) }.size in (1..2)
            }
        }

        return validTiles.onEach { println(it) }.map { it.id.toLong() }.reduce { curr, next -> curr * next }
    }
}

fun main() {
    val testInput = """
        Tile 2311:
        ..##.#..#.
        ##..#.....
        #...##..#.
        ####.#...#
        ##.##.###.
        ##...#.###
        .#.#.#..##
        ..#....#..
        ###...#.#.
        ..###..###

        Tile 1951:
        #.##...##.
        #.####...#
        .....#..##
        #...######
        .##.#....#
        .###.#####
        ###.##.##.
        .###....#.
        ..#.#..#.#
        #...##.#..

        Tile 1171:
        ####...##.
        #..##.#..#
        ##.#..#.#.
        .###.####.
        ..###.####
        .##....##.
        .#...####.
        #.##.####.
        ####..#...
        .....##...

        Tile 1427:
        ###.##.#..
        .#..#.##..
        .#.##.#..#
        #.#.#.##.#
        ....#...##
        ...##..##.
        ...#.#####
        .#.####.#.
        ..#..###.#
        ..##.#..#.

        Tile 1489:
        ##.#.#....
        ..##...#..
        .##..##...
        ..#...#...
        #####...#.
        #..#.#.#.#
        ...#.#.#..
        ##.#...##.
        ..##.##.##
        ###.##.#..

        Tile 2473:
        #....####.
        #..#.##...
        #.##..#...
        ######.#.#
        .#...#.#.#
        .#########
        .###.#..#.
        ########.#
        ##...##.#.
        ..###.#.#.

        Tile 2971:
        ..#.#....#
        #...###...
        #.#.###...
        ##.##..#..
        .#####..##
        .#..####.#
        #..#.#..#.
        ..####.###
        ..#.#.###.
        ...#.#.#.#

        Tile 2729:
        ...#.#.#.#
        ####.#....
        ..#.#.....
        ....#..#.#
        .##..##.#.
        .#.####...
        ####.#.#..
        ##.####...
        ##..#.##..
        #.##...##.

        Tile 3079:
        #.#.#####.
        .#..######
        ..#.......
        ######....
        ####.#..#.
        .#...#.##.
        #.#####.##
        ..#.###...
        ..#.......
        ..#.###...
    """.trimIndent().let { Day20.parseInput(it) }

    println(Day20.part1(Day20.input))
}