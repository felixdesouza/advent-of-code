package aoc2020

import common.readLines

object Day10 {

    val input = readLines("/aoc2020/day10.txt").map { it.toInt() }

    val testInput2 = """
        16
        10
        15
        5
        1
        11
        7
        19
        6
        12
        4
    """.trimIndent()
        .lines().map { it.toInt() }

    val testInput = """
        28
        33
        18
        42
        31
        14
        46
        20
        48
        47
        24
        23
        49
        45
        19
        38
        39
        11
        1
        32
        25
        35
        8
        17
        7
        9
        4
        2
        34
        10
        3
    """.trimIndent()
        .lines().map { it.toInt() }

    fun part1(joltages: List<Int>): Long {
        println(joltages)
        val sorted = joltages.sorted()
        val adapterRating = sorted.max()!! + 3
        val chain = listOf(0).plus(sorted).plus(adapterRating)

        println(sorted)

        val counts = chain.windowed(2) { (a, b) -> b - a}
            .onEach { println(it) }
            .groupingBy { it }
            .eachCount()

        println(counts)
        return counts.getOrDefault(1, 0).toLong() * counts.getOrDefault(3, 0)
    }

    fun part2(joltages: List<Int>): Long {
        val sorted = joltages.sorted()
        val adapterRating = sorted.max()!! + 3
        val chain = listOf(0).plus(sorted).plus(adapterRating)

        val map = mutableMapOf<Int, Long>()

        fun f(index: Int): Long {
            if (map.containsKey(index)) {
                return map[index]!!
            }

            if (index == chain.size - 1) {
                return 1
            }

            val result = chain.withIndex().toList().slice((index + 1 until chain.size))
                .filter { it.value <= chain[index] + 3 }
                .map { f(it.index) }
                .sum()

            map[index] = result
            return result
        }

        return f(0)
    }
}

fun main() {
    println(Day10.part1(Day10.input))
    println(Day10.part2(Day10.input))
}