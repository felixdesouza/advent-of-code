package aoc2022

import common.Coordinate
import common.Problem
import common.repeat
import kotlin.math.sign

object Day9 : Problem() {

    val input = rawInput.let { parse(it) }
    val testInput = """
        R 4
        U 4
        L 3
        D 1
        R 4
        D 1
        L 5
        R 2
    """.trimIndent().let { parse(it) }

    val testInput2 = """
        R 5
        U 8
        L 8
        D 3
        R 17
        D 10
        L 25
        U 20
    """.trimIndent().let { parse(it) }

    private fun parse(input: String): Sequence<Coordinate> = input.lines().asSequence().flatMap {
        val (direction, distance) = it.split(" ")
        val coordinate = when (direction) {
            "R" -> Coordinate(1, 0)
            "L" -> Coordinate(-1, 0)
            "U" -> Coordinate(0, -1)
            "D" -> Coordinate(0, 1)
            else -> throw AssertionError("not recognised")
        }
        sequenceOf(coordinate).repeat().take(distance.toInt())
    }

    fun part1(input: Sequence<Coordinate>): Int {
        return simulate(input, 2)
    }

    fun part2(input: Sequence<Coordinate>): Int {
        return simulate(input, 10)
    }

    private fun simulate(input: Sequence<Coordinate>, n: Int): Int {
        data class State(val tailSeen: Set<Coordinate>, val knots: List<Coordinate>)

        val initialState = State(setOf(Coordinate.origin), sequenceOf(Coordinate.origin).repeat().take(n).toList())

        val finalState = input.fold(initialState) { (seen, knots), next ->
            val (head, rest) = knots.first() to knots.drop(1)
            val newHead = head + next

            data class InnerState(val knots: List<Coordinate>, val prevKnot: Coordinate)

            val initialInnerState = InnerState(listOf(newHead), newHead)

            val finalInnerState = rest.fold(initialInnerState) { (knots, prev), next ->
                val newTail = if (next in prev.allNeighbours() + prev) {
                    next
                } else {
                    val xDirection = (prev.x - next.x).sign
                    val yDirection = (prev.y - next.y).sign
                    next + Coordinate(xDirection, yDirection)
                }
                InnerState(knots + newTail, newTail)
            }

            State(tailSeen = seen + finalInnerState.prevKnot, knots = finalInnerState.knots)
        }

        return finalState.tailSeen.size
    }

    @JvmStatic
    fun main(args: Array<String>) {
        println(part1(input))
        println(part2(input))
    }
}

