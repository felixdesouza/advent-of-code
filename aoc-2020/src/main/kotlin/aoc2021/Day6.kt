package aoc2021

import common.openFile

object Day6 {
    fun parseInput(input: String): List<Int> {
        return input.trim().split(",").map { Integer.parseInt(it) }
    }

    val input = openFile("/aoc2021/day6.txt")
            .let { parseInput(it) }

    val testInput = "3,4,3,1,2"
            .let { parseInput(it) }

    fun part1(input: List<Int>): Int {
        return sequenceGenerator(input)
                .drop(80)
                .first().size
    }

    fun part2(input: List<Int>): Long {
        return  sequenceGenerator2(input)
                .drop(256)
                .first()
                .map { it.value }
                .sum()
    }

    private fun sequenceGenerator(input: List<Int>): Sequence<List<Int>> {
        return generateSequence(input) { prev ->
            val numNewBla = prev.count { it == 0 }
            val newBla = (1..numNewBla).map { 8 }
            prev.map { it - 1 }.map { if (it < 0) 6 else it }.plus(newBla)
        }
    }

    private fun sequenceGenerator2(input: List<Int>): Sequence<Map<Int, Long>> {
        val asCounts = input.groupingBy { it }.eachCount().mapValues { it.value.toLong() }

        return generateSequence(asCounts) { prev ->
            val numNewEntry = 8 to (prev[0] ?: 0)

            prev.entries.map { (key, value) ->
                when (key) {
                    0 -> 6 to value
                    else -> (key - 1) to value
                }
            }.groupingBy { it.first }
                    .fold(0L) { acc, element -> acc + element.second }
                    .plus(numNewEntry)
        }
    }

    @JvmStatic
    fun main(args: Array<String>) {
        println(part1(testInput))
        println(part1(input))
        println(part2(testInput))
        println(part2(input))
    }
}
