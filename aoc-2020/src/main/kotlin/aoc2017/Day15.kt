package aoc2017

import aoc2017.Day15.part1

object Day15 {

    val input = 512 to 191
    val testInput = 65 to 8921
    val factors = 16807L to 48271L
    val modulus = 2147483647

    fun generate(starting: Long, factor: Long): Sequence<Long> {
        return generateSequence(starting) { (it * factor) % modulus }
    }

    fun Pair<Int, Int>.part1(): Int {
        val mask = (1L shl 16) - 1
        val (a, b) = this
        fun Long.lsb() = this.and( mask)
        return generate(a.toLong(), factors.first).zip(generate(b.toLong(), factors.second))
                .take(40_000_000)
                .count { (lsbA, lsbB) -> lsbA.lsb() == lsbB.lsb() }
    }
}

fun main() {
    println(Day15.input.part1())
}