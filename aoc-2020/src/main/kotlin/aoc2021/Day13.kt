package aoc2021

import common.Coordinate
import common.Grid
import common.readLines

object Day13 {

    val input = readLines("/aoc2021/day13.txt").let { Input.parse(it) }

    val testInput = """
        6,10
        0,14
        9,10
        0,3
        10,4
        4,11
        6,0
        6,12
        4,1
        0,13
        10,12
        3,4
        3,0
        8,4
        1,10
        2,14
        8,10
        9,0

        fold along y=7
        fold along x=5
    """.trimIndent()
            .lines()
            .let { Input.parse(it) }

    data class Fold(val value: Int, val horizontal: Boolean)

    data class Input(val coordinates: Set<Coordinate>, val folds: List<Fold>) {
        companion object {
            fun parse(input: List<String>): Input {
                val (coordinates, folds) = input.filter { it.isNotBlank() }.partition { !it.startsWith("fold along ") }
                val parsedCoordinates = coordinates
                        .map { it.split(",").map { Integer.parseInt(it) }.let { (x, y) -> Coordinate(x, y) } }
                        .toSet()
                val parsedFolds = folds.map {
                    it.substringAfter("fold along ").split("=")
                            .let { (direction, value) ->
                                Fold(Integer.parseInt(value), direction == "y")
                            }
                }
                return Input(parsedCoordinates, parsedFolds)
            }
        }
    }

    fun part1(input: Input): Int {
        return input.copy(folds = listOf(input.folds.first())).combine().count()
    }

    private fun Input.combine() = folds.fold(coordinates) { coordinates, fold ->
        when (fold.horizontal) {
            true -> {
                val (above, below) = coordinates.partition { it.y <= fold.value }
                val newBelow = below.map { it.copy(y = fold.value - (it.y - fold.value)) }

                above.plus(newBelow).toSet()
            }
            false -> {
                val (left, right) = coordinates.partition { it.x <= fold.value }
                val newRight = right.map { it.copy(x = fold.value - (it.x - fold.value)) }
                left.plus(newRight).toSet()
            }
        }
    }

    fun part2(input: Input) {
        val coordinates = input.combine()

        val (_, bottomRight) = Coordinate.boundingBox(coordinates)
        val (maxX, maxY) = bottomRight
        val map = mutableMapOf<Coordinate, Char>()
        for (y in 0..maxY) {
            for (x in 0..maxX) {
                val coordinate = Coordinate(x, y)
                val character = if (coordinate in coordinates) '#' else ' '
                map[coordinate] = character
            }
        }

        Grid(map, maxY + 1, maxX + 1).print()
    }

    @JvmStatic
    fun main(args: Array<String>) {
        println(part1(testInput))
        println(part1(input))
        println(part2(testInput))
        println(part2(input))
    }
}
