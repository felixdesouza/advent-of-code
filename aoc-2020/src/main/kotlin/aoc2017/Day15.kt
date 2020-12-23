package aoc2017

import aoc2017.Day15.part2

object Day15 {

    val input = 512 to 191
    val testInput = 65 to 8921
    val factors = 16807L to 48271L
    val modulus = 2147483647
    val mask = (1L shl 16) - 1

    fun generate(starting: Long, factor: Long, multiplier: Int = 1): Sequence<Long> {
        return generateSequence(starting) { (it * factor) % modulus }.drop(1).filter { it % multiplier == 0L }
    }

    private fun Long.lsb() = this.and(mask)

    fun Pair<Int, Int>.part1(): Int = run(1 to 1, 40_000_000)
    fun Pair<Int, Int>.part2(): Int = run(4 to 8, 5_000_000)

    private fun Pair<Int, Int>.run(multiplier: Pair<Int, Int>, n: Int): Int {
        val (a, b) = this
        return generate(a.toLong(), factors.first, multiplier.first).zip(generate(b.toLong(), factors.second, multiplier.second))
                .take(n)
                .count { (lsbA, lsbB) -> lsbA.lsb() == lsbB.lsb() }
    }
}

fun main() {
    println(Day15.input.part2())
}