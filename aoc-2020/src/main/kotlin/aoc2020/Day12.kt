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

    data class WaypointState(val shipCoordinate: Coordinate, val waypointVector: Coordinate)

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

    fun part1(instructions: List<Instruction>): Int {

        val (newCoord, _) = instructions.fold(State(Coordinate.origin, 90)) { currState, next ->
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

        return abs(newCoord.x) + abs(newCoord.y)
    }

    fun part2(instructions: List<Instruction>): Int {
        fun rotate(coordinate: Coordinate, int: Int): Coordinate {
            val check = if (int < 0) {
                (int + 360) % 360
            } else {
                int % 360
            }

            var (x, y) = coordinate

            val times = check / 90

            for (i in (1..times)) {
                val temp = x
                x = -y
                y = temp
            }

            return Coordinate(x, y)
        }
        val (newCoord, _) = instructions.fold(WaypointState(Coordinate.origin, Coordinate(10, -1))) { currState, next ->
            val (ship, waypoint) = currState
            val (action, operand) = next

            when (action) {
                'L' -> currState.copy(waypointVector = rotate(waypoint, 360 - operand))
                'R' -> currState.copy(waypointVector = rotate(waypoint, operand))
                'F' -> currState.copy(shipCoordinate = ship + waypoint * operand)
                'N' -> currState.copy(waypointVector = waypoint + directionToCoord(0) * operand)
                'S' -> currState.copy(waypointVector = waypoint + directionToCoord(180) * operand)
                'E' -> currState.copy(waypointVector = waypoint + directionToCoord(90) * operand)
                'W' -> currState.copy(waypointVector = waypoint + directionToCoord(270) * operand)
                else -> throw AssertionError("invalid $action")
            }

        }
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
    println(Day12.part2(Day12.input))
}