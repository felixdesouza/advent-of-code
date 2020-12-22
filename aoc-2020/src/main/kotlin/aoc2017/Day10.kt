package aoc2017

object Day10 {

    val input = listOf(183, 0, 31, 146, 254, 240, 223, 150, 2, 206, 161, 1, 255, 232, 199, 88)
    val testInput = listOf(3, 4, 1, 5)

    fun List<Int>.splitAt(i: Int): Pair<List<Int>, List<Int>> {
        return slice(0 until i) to slice(i until size)
    }

    fun List<Int>.cycle(i: Int): List<Int> {
        val normalised = ((i % size) + size) % size
        val (tail, head) = splitAt(normalised)
        return (head + tail)
    }

    fun part1(input: List<Int>, n: Int): Int {
        val initialState = (0 until n).toList() to 0
        val (finalState, _) = input.foldIndexed(initialState) { skipSize, (state, position), k ->
            println("before: $state, k: $k, skipSize: $skipSize, position: $position")
            val (toReverse, remainder) = state.cycle(position).splitAt(k)

            (remainder + toReverse.reversed())
                    .cycle(-(position + k)) to (position + k + skipSize) % n
        }

        val (first, second) = finalState
        return first * second
    }
}

fun main() {
    println(Day10.part1(Day10.input, 256))
}