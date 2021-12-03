package aoc2021

import common.readLines

object Day3 {
    fun parseInput(lines: List<String>) = lines.filter { it.trim().isNotEmpty() }
            .onEach { println(it) }
            .map { it.map { (it - '0') } }

    val input = readLines("/aoc2021/day3.txt")
            .let { parseInput(it) }

    val testInput = listOf<String>(
            "00100",
            "11110",
            "10110",
            "10111",
            "10101",
            "01111",
            "00111",
            "11100",
            "10000",
            "11001",
            "00010",
            "01010")
            .let { parseInput(it) }

    fun part1(input: List<List<Int>>): Long {
        val (gamma, epsilon) = gammaEpsilon(input)
        return gamma * epsilon.toLong()
    }

    private fun gammaEpsilon(input: List<List<Int>>): Pair<Int, Int> {
        val combined = input.reduce { acc, next ->
            acc.zip(next).map { (a, n) -> a + n }
        }
        val halfway = input.size / 2
        val gamma = combined.fold(0) { acc, next ->
            println("next $next $halfway ${next / halfway}")
            acc.shl(1) + next / halfway
        }
        val bitSize = input[0].size
        val mask = (1 shl bitSize) - 1
        val epsilon = gamma xor mask
        return Pair(gamma, epsilon)
    }

    fun part2(input: List<List<Int>>): Long {
        val bitSize = input[0].size
        val oxygen = (0 until bitSize).fold(input) { acc, next ->
            val combined = acc.reduce { acc2, next ->
                acc2.zip(next).map { (a, n) -> a + n }
            }
            val ones = combined[next]
            val zeros = acc.size - combined[next]
            val bitMask = if (ones >= zeros) 1 else 0
            acc.filter { it[next] == bitMask }
        }
                .map { it.fold(0) { acc, next -> acc.shl(1) + next } }
                .first()!!

        val co2 = (0 until bitSize).fold(input) { acc, next ->
            if (acc.size == 1) {
                return@fold acc
            }
            val combined = acc.reduce { acc2, next ->
                acc2.zip(next).map { (a, n) -> a + n }
            }
            val ones = combined[next]
            val zeros = acc.size - combined[next]
            val bitMask = if (zeros <= ones) 0 else 1
            acc.filter { it[next] == bitMask }
        }
                .map { it.fold(0) { acc, next -> acc.shl(1) + next } }
                .first()!!
        return oxygen.toLong() * co2
    }

    @JvmStatic
    fun main(args: Array<String>) {
        println(part1(testInput))
        println(part1(input))
        println(part2(testInput))
        println(part2(input))
    }
}