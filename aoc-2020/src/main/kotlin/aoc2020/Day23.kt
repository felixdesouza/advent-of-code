package aoc2020

import aoc2020.Day23.part1
import aoc2020.Day23.part2

object Day23 {

    val input = 362981754
    val testInput = 389125467

    fun List<Int>.splitAt(i: Int): Pair<List<Int>, List<Int>> {
        return subList(0, i) to subList(i, size)
    }

    data class Node(val value: Int, val prev: Int, val next: Int)

    data class Circular(val buffer: MutableMap<Int, Node>, val head: Int) {
        val size = buffer.size
        companion object {
            fun fromList(list: List<Int>): Circular {
                val last = list.last()
                val first = list.first()
                val nodes = mutableMapOf<Int, Node>()
                println("creating")
                (listOf(last) + list + first).windowed(3)
                        .forEach { (prev, curr, next) ->
                            val prevNode = nodes[prev]?.copy(next = curr) ?: Node(prev, -1, curr)
                            val nextNode = nodes[next]?.copy(prev = curr) ?: Node(next, curr, -1)
                            val currNode = Node(curr, prev, next)
                            nodes[prev] = prevNode
                            nodes[next] = nextNode
                            nodes[curr] = currNode
                        }
                println("finished creating")
                return Circular(nodes, first)
            }
        }

        fun first() = head
        fun cycle(i: Int): Circular {
            val newHead = getAtIndex(i).value
            return copy(head = newHead)
        }

        fun withHead(head: Int): Circular {
            return copy(head = head)
        }

        fun getAtIndex(i: Int): Node {
            return generateSequence(buffer[head]) { buffer[it.next] }.drop(i).first()
        }

        fun subList(n: Int): List<Int> {
            return generateSequence(head) { buffer[it]!!.next }.take(n).toList()
        }

        fun max(cutoff: Int): Int {
            return generateSequence(head) { buffer[it]!!.next }.take(cutoff).max()!!
        }

        fun toList(): List<Int> {
            return subList(size)
        }

        fun move(): Circular {
            val current = first()
            val cycle = cycle(1)
            val toTake = cycle.subList(3)
            val destinationCup = (current - 1 downTo 1).firstOrNull { it !in toTake } ?: cycle.cycle(3).max(size - 3)

            val (toTakeFirst, _, toTakeLast) = toTake.map { buffer[it]!! }
            val mutable = buffer
            mutable[toTakeFirst.prev] = buffer[toTakeFirst.prev]!!.copy(next = toTakeLast.next)
            mutable[toTakeLast.next] = buffer[toTakeLast.next]!!.copy(prev = toTakeFirst.prev)

            val destinationCupNode = mutable[destinationCup]!!
            val newDestinationCup = destinationCupNode.copy(next = toTakeFirst.value)
            val newDestinationCupNext = mutable[destinationCupNode.next]!!.copy(prev = toTakeLast.value)
            val newToTakeFirst = toTakeFirst.copy(prev = destinationCup)
            val newToTakeLast = toTakeLast.copy(next = destinationCupNode.next)
            sequenceOf(newDestinationCupNext, newToTakeFirst, newToTakeLast, newDestinationCup).forEach { mutable[it.value] = it }
            return copy(head = current).cycle(1)
        }
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

        // 3: if 2, 1 in toTake, then do max
        // 26: if 25
        val destinationCup = (current - 1 downTo 1).firstOrNull { it !in toTake } ?: remainder.max()
        val (destinationCupList, rest) = remainder.cycle(remainder.indexOf(destinationCup)).splitAt(1)
        val assembled = destinationCupList + toTake + rest
        val next = assembled.indexOf(current)
        return assembled.cycle(next + 1)
    }

    fun Int.digits() = toString().map { it - '0' }
    fun List<Int>.asInt() = joinToString("").toInt()

    fun Int.part1(n: Int): Int {
        val circular = digits().let { Circular.fromList(it) }
        return generateSequence(circular) { it.move() }.drop(n).first().let {
            val digits = it.toList()
            digits.cycle(digits.indexOf(1)).drop(1).asInt()
        }
    }

    fun Int.part2(n: Int = 10_000_000): Long {
        val array = digits() + (10..1_000_000)

        val (result, _) = generateSequence(Circular.fromList(array) to 0) { (prev, i) ->
            val next = prev.move()
            if (i % 1000000 == 0) println(i)
            next to i + 1
        }.drop(n).first()

        val (first, second) = result.withHead(1).cycle(1).subList(2)
        println("$first, $second")

        return first.toLong() * second.toLong()
    }
}

fun main() {
    println(Day23.input.part1(100))
    println(Day23.input.part2())
}