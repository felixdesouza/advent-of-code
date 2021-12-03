package aoc2021

import common.readLines

object Day3 {
    fun parseInput(lines: List<String>) = lines.filter { it.trim().isNotEmpty() }
            .map { it.map { (it - '0') } }

    val input = readLines("/aoc2021/day3.txt")
            .let { parseInput(it) }

    val testInput = listOf("00100", "11110", "10110", "10111", "10101", "01111", "00111", "11100", "10000", "11001",
            "00010", "01010")
            .let { parseInput(it) }

    fun part1(input: List<List<Int>>): Long {
        val onesZeros = onesZeros(input)
        val gamma = onesZeros.map { (one, zero) -> if (one > zero) 1 else 0 }
                .joinToString(separator = "") { it.toString() }
                .let { Integer.parseInt(it, 2) }
        val bitSize = input[0].size
        val mask = (1 shl bitSize) - 1
        val epsilon = gamma xor mask
        return gamma * epsilon.toLong()
    }

    private fun onesZeros(input: List<List<Int>>): List<Pair<Int, Int>> {
        val ones = input.reduce { acc, next -> acc.zip(next).map { (a, n) -> a + n } }
        val zeros = ones.map { input.size - it }
        return ones.zip(zeros)
    }

    fun part2(input: List<List<Int>>): Long {
        val oxygen = part2Calc(input) { (ones, zeros) -> ones < zeros }
        val co2 = part2Calc(input) { (ones, zeros) -> zeros <= ones }
        return oxygen.toLong() * co2
    }

    private fun part2Calc(input: List<List<Int>>, predicate: (Pair<Int, Int>) -> Boolean): Int {
        val bitSize = input[0].size
        return (0 until bitSize)
                .fold(input) { acc, next ->
                    if (acc.size == 1) {
                        return@fold acc
                    }
                    val bitMask = if (predicate(onesZeros(acc)[next])) 0 else 1
                    acc.filter { it[next] == bitMask }
                }
                .map { Integer.parseInt(it.joinToString("") { it.toString() }, 2) }
                .first()!!
    }

    @JvmStatic
    fun main(args: Array<String>) {
        println(part1(testInput))
        println(part1(input))
        println(part2(testInput))
        println(part2(input))
    }
}