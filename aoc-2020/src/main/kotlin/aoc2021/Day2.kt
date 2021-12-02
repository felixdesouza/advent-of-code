package aoc2021

import common.readLines

object Day2 {

    fun parseInput(lines: List<String>) = lines
            .map { it.split(" ") }
            .map { (a, b) -> a to b.toInt() }

    val input = readLines("/aoc2021/day2.txt")
            .let { parseInput(it) }

    val testInput = listOf("forward 5", "down 5", "forward 8", "up 3", "down 8", "forward 2")
            .let { parseInput(it) }

    fun part1(input: List<Pair<String, Int>>): Long {
        return input.fold(Pair(0, 0)) { position, next ->
            val (instruction, quantity) = next
            when (instruction) {
                "forward" -> position.copy(first = position.first + quantity)
                "down" -> position.copy(second = position.second + quantity)
                "up" -> position.copy(second = position.second - quantity)
                else -> throw AssertionError("unexpected")
            }
        }.let { (pos, depth) -> pos.toLong() * depth }
    }

    fun part2(input: List<Pair<String, Int>>): Long {
        return input.fold(Triple(0, 0, 0)) { position, next ->
            val (instruction, quantity) = next
            val (horizontal, depth, aim) = position
            when (instruction) {
                "forward" -> position.copy(first = horizontal + quantity, second = depth + quantity * aim)
                "down" -> position.copy(third = aim + quantity)
                "up" -> position.copy(third = aim - quantity)
                else -> throw AssertionError("unexpected")
            }
        }.let { (pos, depth) -> pos.toLong() * depth }
    }

    @JvmStatic
    fun main(args: Array<String>) {
        part1(input).also { println(it) }
        part1(testInput).also { println(it) }
        part2(testInput).also { println(it) }
        part2(input).also { println(it) }
    }
}