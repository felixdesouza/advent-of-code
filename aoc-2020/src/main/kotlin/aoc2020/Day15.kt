package aoc2020

object Day15 {

    val input = "1,0,15,2,10,13".let { parseInput(it) }

    fun parseInput(list: String) = list.split(",").map { it.toInt() }

    fun part1(input: List<Int>): Long {
        return f(input, 2020)
    }

    fun part2(input: List<Int>): Long {
        return f(input, 30000000)
    }

    data class Holder(var a: Int?, var b: Int? = null) {
        fun add(i: Int) {
            return when {
                a == null -> a = i
                b == null -> b = i
                else -> {
                    a = b
                    b = i
                }
            }
        }

        fun first() = b == null

        fun diff() = if (b != null && a != null) b!! - a!! else null
    }

    private fun f(input: List<Int>, p: Int): Long {
        val map: MutableMap<Int, Holder> =
            input.withIndex().map { (index, value) -> value to Holder(a = index + 1) }.toMap().toMutableMap()

        var turn = input.size + 1
        var lastNumberSpoken = input.last()

        while (turn <= p) {
            val queue = map.computeIfAbsent(lastNumberSpoken) { Holder(a = turn) }

            lastNumberSpoken = when {
                queue.first() -> 0
                else -> queue.diff()!!
            }
            map.computeIfAbsent(lastNumberSpoken) { Holder(a = turn) }.add(turn)

            turn += 1
        }

        return lastNumberSpoken.toLong()
    }


}

fun main() {
    val testInput = listOf(0, 3, 6)
    println(Day15.part2(Day15.input))
//    println(Day15.part2(listOf(2, 3, 1)))
}