package aoc2020

import common.Coordinate
import common.Coordinate3d
import common.Grid
import common.openFile

object Day17 {

    val testInput = """
        .#.
        ..#
        ###
    """.trimIndent().let{ parseInput(it)}

    val input = openFile("/aoc2020/day17.txt").let { parseInput(it) }

    fun parseInput(raw: String): Set<Coordinate3d> {
        return Grid.parse(raw) {
            when (it) {
                '#' -> true
                else -> false
            }
        }.grid.filterValues { it }.keys.map { (x, y) -> Coordinate3d(x, y, 0) }.toSet()
    }

    fun iterate(cubes: Set<Coordinate3d>): Set<Coordinate3d> {
        val (extremeMin, extremeMax) = Coordinate3d.boundingBox(cubes)

        val coords = mutableListOf<Coordinate3d>()
        for (newZ in (extremeMin.z - 2..extremeMax.z + 2)) {
            for (newY in (extremeMin.y - 2..extremeMax.y + 2)) {
                for (newX in (extremeMin.x - 2..extremeMax.x + 2)) {
                    coords.add(Coordinate3d(newX, newY, newZ))
                }
            }
        }

        return coords.filter { coord ->
            val active = coord in cubes
            if (active) {
                coord.neighbours().intersect(cubes).size in (2 ..3)
            } else {
                coord.neighbours().intersect(cubes).size == 3
            }
        }.toSet()
    }

    fun part1(initial: Set<Coordinate3d>): Int {
        return generateSequence(initial) { iterate(it) }.take(7).last().size
    }

}

fun main() {
    println(Day17.part1(Day17.input))
}