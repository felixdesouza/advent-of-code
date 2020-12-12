package aoc2020

import common.Coordinate
import common.readLines
import java.lang.Math.abs

object Day12 {

    val input = readLines("/aoc2020/day12.txt").map { parseInput(it) }

    fun parseInput(line: String): Instruction {
        return Instruction(line.first(), line.substring(1).toInt())
    }

    data class Instruction(val action: Char, val operand: Int)

    data class State(val coordinate: Coordinate, val direction: Int)

    fun part1(instructions: List<Instruction>): Int {
        fun directionToCoord(int: Int): Coordinate {
            val check = if (int < 0) {
                (int + 360) % 360
            } else {
                int % 360
            }
            return when (check) {
                0 -> Coordinate(0, -1)
                90 -> Coordinate(1, 0)
                180 -> Coordinate(0, 1)
                270 -> Coordinate(-1, 0)
                else -> throw AssertionError("unexpected $int $check")
            }
        }
        val (newCoord, newDirection) = instructions.fold(State(Coordinate.origin, 90)) { currState, next ->
            val (coord, direction) = currState
            val (action, operand) = next
            when (action) {
                'L' -> currState.copy(direction = (currState.direction - operand) % 360)
                'R' -> currState.copy(direction = (currState.direction + operand) % 360)
                'F' -> currState.copy(coordinate = coord + directionToCoord(direction) * operand)
                'N' -> currState.copy(coordinate = coord + directionToCoord(0) * operand)
                'S' -> currState.copy(coordinate = coord + directionToCoord(180) * operand)
                'E' -> currState.copy(coordinate = coord + directionToCoord(90) * operand)
                'W' -> currState.copy(coordinate = coord + directionToCoord(270) * operand)
                else -> throw AssertionError("invalid $action")
            }
        }

        println(newCoord)
        println(newDirection)

        return abs(newCoord.x) + abs(newCoord.y)
    }

}

fun main() {
    val testInput = """
        F10
        N3
        F7
        R90
        F11
    """.trimIndent().lines().map { Day12.parseInput(it) }
    println(Day12.part1(Day12.input))
}