package aoc2020

import aoc2020.Day23.part1

object Day23 {

    val input = 362981754
    val testInput = 389125467

    fun List<Int>.splitAt(i: Int): Pair<List<Int>, List<Int>> {
        return slice(0 until i) to slice(i until size)
    }

    fun List<Int>.cycle(i: Int): List<Int> {
        val normalised = ((i % size) + size) % size
        val (tail, head) = splitAt(normalised)
        return (head + tail)
    }

    fun List<Int>.move(): List<Int> {
        val current = first()
        val cycle = cycle(1)
        val (toTake, remainder) = cycle.splitAt(3)
        val destinationCup = (current - 1 downTo 1).firstOrNull { it in remainder } ?: remainder.max()
        println("this = $this, toTake = ${toTake}, destination - $destinationCup")
        val (destinationCupList, rest) = remainder.cycle(remainder.indexOf(destinationCup)).splitAt(1)
        val assembled = destinationCupList + toTake + rest
        val next = assembled.indexOf(current)
        return assembled.cycle(next + 1).also { println("$it\n") }
    }

    fun Int.move() = digits().move().asInt()

    fun Int.digits() = toString().map { it - '0' }
    fun List<Int>.asInt() = joinToString("").toInt()

    fun Int.part1(n: Int): Int {
        return generateSequence(this) { it.move() }.drop(n).first()
                .let { val digits = it.digits()
                    digits.cycle(digits.indexOf(1)).drop(1).asInt()
                }
                .also { println(it) }
    }
}

fun main() {
//    println(Day23.testInput.part1(10))
    println(Day23.input.part1(100))
//    println(Day23.testInput.move().move())
}