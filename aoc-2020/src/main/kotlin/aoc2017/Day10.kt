package aoc2017

import kotlin.streams.toList

object Day10 {

    val input = "183,0,31,146,254,240,223,150,2,206,161,1,255,232,199,88"
    val testInput = "3,4,1,5"

    fun List<Int>.splitAt(i: Int): Pair<List<Int>, List<Int>> {
        return slice(0 until i) to slice(i until size)
    }

    fun List<Int>.cycle(i: Int): List<Int> {
        val normalised = ((i % size) + size) % size
        val (tail, head) = splitAt(normalised)
        return (head + tail)
    }

    fun part1(input: List<Int>, n: Int): Int {
        val (finalState, _) = hash(input, n, (0 until n).toList(), 0)

        val (first, second) = finalState
        return first * second
    }

    private fun hash(input: List<Int>, n: Int, initialState: List<Int>, currentPosition: Int, previousSkipSize: Int = 0): Triple<List<Int>, Int, Int> {
        return input.fold(Triple(initialState, currentPosition, previousSkipSize)) { (state, position, skipSize), k ->
            val (toReverse, remainder) = state.cycle(position).splitAt(k)

            Triple((remainder + toReverse.reversed()).cycle(-(position + k)), (position + k + skipSize) % n, skipSize + 1)
        }
    }

    fun part2(input: String, n: Int = 256): String {
        val convertedInput = input.chars().toList() + listOf(17,31,73,47,23)

        tailrec fun iterate(state: List<Int>, currentPosition: Int, currentSkipSize: Int, count: Int): List<Int> {
            return when (count) {
                0 -> state
                else -> {
                    val (newState, newPosition, newSkipSize) = hash(convertedInput, n, state, currentPosition, currentSkipSize)
                    iterate(newState, newPosition, newSkipSize, count - 1)
                }
            }
        }

        val sparseHash = iterate((0 until n).toList(), 0, 0, 64)
        val denseHash = sparseHash.chunked(16).map { it.reduce { a, b -> a xor b } }

        return denseHash.joinToString("") { Integer.toHexString(it).padStart(2, '0') }
    }
}

fun main() {
    println(Day10.part1(Day10.input.split(",").map { it.toInt() }, 256))
    println(Day10.part2(Day10.input))
}