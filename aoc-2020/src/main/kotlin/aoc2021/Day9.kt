package aoc2021

import com.google.common.collect.Queues
import common.Coordinate
import common.Grid
import common.openFile

object Day9 {
    fun parseInput(input: String): Grid<Int> = Grid.parse(input) { c -> c - '0' }

    val input = openFile("/aoc2021/day9.txt")
            .let { parseInput(it) }

    val testInput = """
        2199943210
        3987894921
        9856789892
        8767896789
        9899965678
    """.trimIndent()
            .let { parseInput(it) }

    fun part1(input: Grid<Int>): Int {
        return lowPoints(input).sumBy { input[it]!! + 1 }
    }

    private fun lowPoints(input: Grid<Int>): List<Coordinate> {
        return input
                .filter { coordinate, value ->
                    coordinate.neighbours()
                            .map { input[it] ?: Integer.MAX_VALUE }
                            .all { it > value }
                }
                .keys.toList()
    }

    private fun findBasinForLowPoint(input: Grid<Int>, coordinate: Coordinate): Map<Coordinate, Int> {
        val queue = Queues.newArrayDeque<Coordinate>(setOf(coordinate))
        val visited = mutableSetOf<Coordinate>()

        while (queue.isNotEmpty()) {
            val head = queue.pop()
            if (head in visited) continue
            visited.add(head)

            head.neighbours()
                    .filter { (input[it] ?: 9) < 9 }
                    .filterNot { it in visited }
                    .forEach { queue.add(it) }
        }

        return visited.associateWith { input[it]!! }
    }

    fun part2(input: Grid<Int>): Int {
        val lowPoints = lowPoints(input)

        return lowPoints.map { findBasinForLowPoint(input, it) }
                .map { it.size }
                .sortedDescending()
                .take(3)
                .fold(1) { acc, next -> acc * next }
    }

    @JvmStatic
    fun main(args: Array<String>) {
        println(part1(testInput))
        println(part1(input))
        println(part2(testInput))
        println(part2(input))
    }
}
