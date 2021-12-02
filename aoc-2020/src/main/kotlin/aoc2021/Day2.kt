package aoc2021

import common.readLines

object Day2 {

    val input = readLines("/aoc2021/day2.txt")
            .map { it.split(" ") }
            .map { (a, b) -> a to b.toInt() }

    val testInput = listOf<String>(
            "forward 5",
            "down 5",
            "forward 8",
            "up 3",
            "down 8",
            "forward 2"
    ).map { it.split(" ") }
            .map { (a, b) -> a to b.toInt() }

    fun part1(input: List<Pair<String, Int>>): Long {
        return input.fold(Pair(0, 0)) { position, next ->
            val (instruction, quantity) = next
            val bla = if (instruction == "forward") {
                position.copy(first = position.first + quantity)
            } else if (instruction == "down") {
                position.copy(second = position.second + quantity)
            } else {
                position.copy(second = position.second - quantity)
            }
            bla.also { println(it) }
        }.let { (pos, depth) -> pos.toLong() * depth }
    }

    fun part2(input: List<Pair<String, Int>>): Long {
        return input.fold(Triple(0, 0, 0)) { position, next ->
            val (instruction, quantity) = next
            val bla = if (instruction == "forward") {
                position.copy(first = position.first + quantity, second = position.second + quantity * position.third)
            } else if (instruction == "down") {
                position.copy(third = position.third + quantity)
            } else {
                position.copy(third = position.third - quantity)
            }
            bla.also { println(it) }
        }.let { (pos, depth) -> pos.toLong() * depth }
    }

    @JvmStatic
    fun main(args: Array<String>) {
        part1(input).also { println(it) }
        part2(testInput).also { println(it) }
        part2(input).also { println(it) }
    }
}