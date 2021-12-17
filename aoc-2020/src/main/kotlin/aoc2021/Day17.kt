package aoc2021

import common.Coordinate
import java.lang.Math.abs

object Day17 {

    val input = "target area: x=137..171, y=-98..-73"
            .let { Input.parse(it) }

    val testInput = "target area: x=20..30, y=-10..-5"
            .let { Input.parse(it) }

    data class Input(val topLeft: Coordinate, val bottomRight: Coordinate) {
        companion object {
            fun parse(input: String): Input {
                val (xBounds, yBounds) = input.drop(13).split(", ")
                        .map { it.drop(2).split("..").map { Integer.parseInt(it) } }

                val (extremeLeft, extremeRight) = xBounds
                val (extremeBottom, extremeTop) = yBounds
                val topLeft = Coordinate(extremeLeft, extremeTop)
                val bottomRight = Coordinate(extremeRight, extremeBottom)

                return Input(topLeft, bottomRight)
            }
        }

        fun minXVelocity(): Int = (1..bottomRight.x).first { (it * (it + 1)) / 2 >= topLeft.x }

        fun simulate(velocity: Coordinate): Boolean {
            val coord = generateSequence(Coordinate.origin to velocity) { (cur, vel) ->
                val newPoint = cur + vel
                val newXVel = when (vel.x) {
                    in 1..Int.MAX_VALUE -> vel.x - 1
                    in Int.MIN_VALUE..(-1) -> vel.x + 1
                    else -> 0
                }
                if (newPoint.x > bottomRight.x || newPoint.y < bottomRight.y) {
                    null
                } else {
                    val newVel = Coordinate(x = newXVel, y = vel.y - 1)
                    newPoint to newVel
                }
            }.map { it.first }
                    .filter { it.x in topLeft.x..bottomRight.x }
                    .filter { it.y in (topLeft.y.downTo(bottomRight.y)) }
                    .lastOrNull()
            println(coord)
            return coord != null
        }
    }

    fun part1(input: Input): Int {
        val diff = abs(input.bottomRight.y)
        return ((diff - 1) * diff) / 2
    }

    fun part2(input: Input): Int {
        val maxYVelocity = abs(input.bottomRight.y) - 1
        return (input.minXVelocity()..input.bottomRight.x)
                .flatMap { x -> maxYVelocity.downTo(input.bottomRight.y).map { y -> Coordinate(x, y) } }
                .count { input.simulate(it) }
    }

    @JvmStatic
    fun main(args: Array<String>) {
        println(part1(testInput))
        println(part1(input))
        println(part2(testInput))
        println(part2(input))
    }

}
