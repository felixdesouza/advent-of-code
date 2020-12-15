package aoc2020

import java.util.*

object Day15 {

    val input = "1,0,15,2,10,13".let { parseInput(it) }

    fun parseInput(list: String) = list.split(",").map { it.toInt() }

    fun part1(input: List<Int>): Long {
        val map: MutableMap<Int, Queue<Int>> = input.withIndex().map { (index, value) -> value to LinkedList(listOf(index + 1)) }.toMap().toMutableMap()

        var turn = input.size + 1
        var lastNumberSpoken = input.last()

        while (turn <= 2020) {
            val queue = map.computeIfAbsent(lastNumberSpoken) { LinkedList() }

            if (queue.size < 2) {
                lastNumberSpoken = 0
            } else {
                val (prev, prevprev) = queue.reversed()
                lastNumberSpoken = prev - prevprev
            }
            map.computeIfAbsent(lastNumberSpoken) { LinkedList() }.offer(turn)

//            println("turn $turn: $lastNumberSpoken")
            turn += 1
        }

        return lastNumberSpoken.toLong()
    }


}

fun main() {
    println(Day15.part1(Day15.input))
}