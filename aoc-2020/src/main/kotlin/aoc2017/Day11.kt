package aoc2017

import common.openFile
import java.lang.Integer.max
import java.lang.Integer.min

object Day11 {

    val input = openFile("/aoc2017/day11.txt").parseInput()

    fun String.parseInput() = split(",")

    fun Map<String, Int>.iterate() = shortcut("nw", "ne", "n")
            .shortcut("se", "sw", "s")
            .opposite("ne", "sw")
            .opposite("nw", "se")
            .opposite("n", "s")
            .shortcut("ne", "s", "se")
            .shortcut("nw", "s", "sw")
            .shortcut("se", "n", "ne")
            .shortcut("sw", "n", "nw")

    private fun Map<String, Int>.reduce() = generateSequence(this) { iterate() }.windowed(2).first { (a, b) -> a == b}.first()

    private fun Map<String, Int>.steps() = values.sum()

    fun part1(input: List<String>): Int {
        val counts = input.groupingBy { it }.eachCount()
        return counts.reduce().steps()
    }

    fun part2(input: List<String>): Int {
        val (_, maxSteps) = input.fold(emptyMap<String, Int>() to 0) { (state, maxSteps), next ->
            val newState = (state + (next to (state[next] ?: 0) + 1)).reduce()
            newState to max(maxSteps, newState.steps())
        }

        return maxSteps
    }

    private fun Map<String, Int>.shortcut(direction1: String, direction2: String, resolved: String): Map<String, Int> {
        val resolvedAdditions = min(this[direction1] ?: 0, this[direction2] ?: 0)
        return listOf(direction1, direction2).fold(this) { state, next ->
            state + (next to (state[next] ?: 0) - resolvedAdditions)
        }.let { it + (resolved to (it[resolved] ?: 0) + resolvedAdditions) }
    }

    private fun Map<String, Int>.opposite(direction1: String, direction2: String): Map<String, Int> {
        val changes = min(this[direction1] ?: 0, this[direction2] ?: 0)
        return listOf(direction1, direction2).fold(this) { state, next ->
            state + (next to (state[next] ?: 0) - changes)
        }
    }
}

fun main() {
    Day11.part1(Day11.input).also { println(it) }
    Day11.part2(Day11.input).also { println(it) }
}