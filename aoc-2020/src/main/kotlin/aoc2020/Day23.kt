package aoc2020

import aoc2020.Day23.part1
import aoc2020.Day23.part2
import kotlin.math.abs
import kotlin.time.ExperimentalTime

@ExperimentalTime
object Day23 {

    val input = 362981754
    val testInput = 389125467

    fun List<Int>.splitAt(i: Int): Pair<List<Int>, List<Int>> {
        return subList(0, i) to subList(i, size)
    }

    data class Circular(val buffer: IntArray, var head: Int) {
        val size = buffer.size - 1

        companion object {
            @ExperimentalTime
            fun fromInputList(list: List<Int>, part2: Boolean): Circular {
                val last = if (part2) 1_000_000 else list.last()
                val first = list.first()
                val nodes = IntArray(if (part2) last + 1 else list.size + 1) { it + 1 }

                nodes[last] = first
                nodes[0] = 0
                if (part2) nodes[list.last()] = 10
                list.windowed(2).forEach { (curr, next) -> nodes[curr] = next }

                return Circular(nodes, first)
            }
        }

        fun first() = head

        fun cycle(i: Int): Circular {
            val newHead = getAtIndex(i)
            return copy(head = newHead)
        }

        fun withHead(head: Int): Circular {
            return copy(head = head)
        }

        fun getAtIndex(i: Int): Int {
            val norm = i % size
            val forward = ((norm + size) % size)

            var curr = head
            for (unused in (0 until abs(forward))) {
                curr = buffer[curr]
            }

            return curr
        }

        fun subList(n: Int): List<Int> {
            return generateSequence(head) { buffer[it] }.take(n).toList()
        }

        fun toList(): List<Int> {
            return subList(size)
        }

        fun move() {
            val current = head

            val toTakeFirst = buffer[current]
            val toTakeSecond = buffer[toTakeFirst]
            val toTakeLast = buffer[toTakeSecond]
            val next = buffer[toTakeLast]

            val destinationCup = (current - 1 downTo 1).firstOrNull { it != toTakeFirst && it != toTakeSecond && it != toTakeLast }
                    ?: (size downTo 1).first { it != toTakeFirst && it != toTakeSecond && it != toTakeLast }

            buffer[current] = buffer[toTakeLast]
            buffer[toTakeLast] = buffer[destinationCup]
            buffer[destinationCup] = toTakeFirst

            head = next
        }
    }

    fun List<Int>.cycle(i: Int): List<Int> {
        val normalised = ((i % size) + size) % size
        val (tail, head) = splitAt(normalised)
        return (head + tail)
    }

    fun Int.digits() = toString().map { it - '0' }
    fun List<Int>.asInt() = joinToString("").toInt()

    fun Int.part1(n: Int): Int {
        val circular = digits().let { Circular.fromInputList(it, false) }
        return generateSequence(circular) { circular.move(); circular }.drop(n).first().let {
            val digits = it.toList()
            digits.cycle(digits.indexOf(1)).drop(1).asInt()
        }
    }

    fun Int.part2(n: Int = 10_000_000): Long {
        var start = System.nanoTime()
        val array = digits()
        (System.nanoTime() - start).also { println("total: $it") }

        start = System.nanoTime()
        val curr = Circular.fromInputList(array, true)
        (System.nanoTime() - start).also { println("total: $it") }

        start = System.nanoTime()
        for (i in 0 until n) {
            curr.move()
        }
        (System.nanoTime() - start).also { println("total: $it, per inv: ${it.toDouble() / n}") }

        val (first, second) = curr.withHead(1).cycle(1).also {  }.subList(2)
        println("$first, $second")

        return first.toLong() * second.toLong()
    }
}

@ExperimentalTime
fun main() {
    println(Day23.input.part1(100))
    println(Day23.input.part2())
}