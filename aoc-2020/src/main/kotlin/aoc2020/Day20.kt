package aoc2020

import com.google.common.collect.ImmutableMap
import common.Coordinate
import common.Grid
import common.openFile

object Day20 {

    val input = openFile("/aoc2020/day20.txt").let { parseInput(it) }

    val seaMonster = """
                          # 
        #    ##    ##    ###
         #  #  #  #  #  #   
    """.trimIndent().let { Grid.parse(it) { it == '#' } }.grid.filter { (_, present) -> present }.keys

    enum class Orientation {
        Original, Rotate90, Rotate180, Rotate270, FlipX, FlipY, Transpose, TransposeRotate
    }

    data class Tile(val id: Int, val orientation: Orientation, private val grid: Grid<Int>) {

        val north: Int by lazy { border(Direction.N) }
        val east: Int by lazy { border(Direction.E) }
        val south: Int by lazy { border(Direction.S) }
        val west: Int by lazy { border(Direction.W) }

        fun orientations(): Set<Tile> {
            return Orientation.values().map { this.copy(orientation = it) }.toSet()
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

        fun applyOrientation(coordinate: Coordinate): Coordinate {
            val (x, y) = coordinate

            fun Coordinate.rotate90(): Coordinate {
                return Coordinate(this.y, grid.numRows - this.x - 1)
            }
            return when (orientation) {
                Orientation.Original -> coordinate
                Orientation.Rotate90 -> coordinate.rotate90()
                Orientation.Rotate180 -> coordinate.rotate90().rotate90()
                Orientation.Rotate270 -> coordinate.rotate90().rotate90().rotate90()
                Orientation.FlipX -> Coordinate(grid.numColumns - x - 1, y)
                Orientation.FlipY -> Coordinate(x, grid.numRows - y - 1)
                Orientation.Transpose -> Coordinate(y, x)
                Orientation.TransposeRotate -> Coordinate(y, x).rotate90().rotate90()
            }
        }

        fun border(direction: Direction): Int {
            return when (direction) {
                Direction.N -> (0 until grid.numColumns).map { grid[applyOrientation(Coordinate(it, 0))] }.joinToString("").toInt(2)
                Direction.S -> (0 until grid.numColumns).map { grid[applyOrientation(Coordinate(it, grid.numRows - 1))] }.joinToString("").toInt(2)
                Direction.E -> (0 until grid.numRows).map { grid[applyOrientation(Coordinate(grid.numColumns - 1, it))] }.joinToString("").toInt(2)
                Direction.W -> (0 until grid.numColumns).map { grid[applyOrientation(Coordinate(0, it))] }.joinToString("").toInt(2)

            }
        }

        fun grid(): Grid<Int> {
            return grid.mapGrid { coordinate, _ -> grid[applyOrientation(coordinate)]!! }
        }

        fun print() {
            grid().mapGrid { _, i -> if (i == 1) "#" else "." }.print()
        }

        fun removeBorder(): Tile {
            val newGrid = (1 until grid.numRows - 1).flatMap { y ->
                (1 until grid.numColumns - 1).map { x ->
                    Coordinate(x - 1, y - 1) to grid[Coordinate(x, y)]!!
                }
            }.toMap().let { Grid(it.toMutableMap(), grid.numRows - 2, grid.numColumns - 2) }

            return copy(grid = newGrid)
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

        return Tile(id, Orientation.Original, grid)
    }

    fun part1(input: Set<Tile>): Long {
        val validTiles = corners(input)

        return validTiles.keys.onEach { println(it) }.map { it.id.toLong() }.reduce { curr, next -> curr * next }
    }

    private fun corners(input: Set<Tile>): Map<Tile, Set<Direction>> {
        return input.mapNotNull { tile ->
            val otherTiles = input - tile

            tile.orientations()
                    .associateWith { oriented ->
                        otherTiles.flatMap { otherTile -> oriented.fitsWith(otherTile) }.toSet()
                    }
                    .filterValues { it.size == 2 }
                    .entries.firstOrNull()
        }.let { ImmutableMap.copyOf(it) }
    }

    fun part2(input: Set<Tile>): Long {
        println("finding corners")
        val corners = corners(input)

        println("assembling")
        val northWest = corners.entries.first { it.value == setOf(Direction.E, Direction.S) }.key
        val otherCorners = (corners - northWest).keys

        val others = input.filter { it.id != northWest.id }
        val westConnections = others.flatMap { it.orientations() }.groupBy { it.west }

        var current = northWest
        val eastMap = mutableMapOf<Tile, Tile>()

        val otherCornerOrientations = otherCorners.flatMap { it.orientations() }
        while (current !in otherCornerOrientations) {
            val eastConnection = westConnections[current.east]?.filter { it.id !in eastMap.keys.map { it.id }.toSet() && it.id != current.id }
            if (eastConnection == null || eastConnection.size != 1) throw AssertionError("unexpected $eastConnection")

            eastMap[current] = eastConnection.first()
            current = eastConnection.first()
        }

        val firstRow = generateSequence(northWest) { eastMap[it] }.toList()
        val northConnections = others.flatMap { it.orientations() }.groupBy { it.north }

        val southMap = mutableMapOf<Tile, Tile>()
        fun goSouth(tile: Tile) {
            var current = tile
            while (true) {
                val southConnection = northConnections[current.south]?.filter { it.id !in southMap.keys.map { it.id }.toSet() && it.id != current.id }
                if (southConnection == null || southConnection.isEmpty()) {
                    break
                }
                if (southConnection.size != 1) throw AssertionError("unexpected $southConnection")

                southMap[current] = southConnection.first()
                current = southConnection.first()
            }
        }

        val assembled = firstRow.asSequence()
                .onEach { goSouth(it) }
                .map {
                    generateSequence(it) { southMap[it] }
                            .onEach { println(it.id) }.toList()
                }
                .onEach { println() }
                .map { it.map { it.removeBorder() } }
                .withIndex()
                .map { (x, rows) ->
                    rows.withIndex().map { (y, cell) ->
                        val grid = cell.grid()
                        val offset = Coordinate(x * grid.numRows, y * grid.numColumns)
                        grid.grid.mapKeys { (coord, _) -> coord + offset }
                    }.reduce { acc, map -> acc + map }
                }.reduce { acc, map -> acc + map }
                .let { Tile(
                        0,
                        Orientation.Original,
                        Grid(it.toMutableMap(), it.keys.map { it.y }.max()!! + 1, it.keys.map { it.x }.max()!! + 1)) }

        val seaMonsterBoundingBox = Coordinate.boundingBox(seaMonster)
        val correctOrientations = assembled.orientations()
                .associateWith { tile ->
                    val grid = tile.grid()
                    val gridKeys = grid.grid.keys
                    val keysToCheck = gridKeys.filter { seaMonsterBoundingBox.second + it in gridKeys }
                    keysToCheck.filter { source -> seaMonster.map { it + source }.all { grid[it]?.equals(1) ?: false } }
                    .associateWith { source -> seaMonster.map { it + source } }
                }
                .filterValues { it.isNotEmpty() }

        val (tile, coords) = correctOrientations.entries.first()
        tile.print()

        val gridCoords = tile.grid().grid.filter { (_, value) -> value == 1 }.keys
        val remaining = gridCoords - coords.values.flatten()
        return remaining.size.toLong()
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

    println(Day20.part2(Day20.input))
}