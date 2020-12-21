package aoc2017

import common.openFile

object Day6 {
    val input = openFile("/aoc2017/day6.txt")
            .split("\\s+".toRegex())
            .map { it.toInt() }

    private fun List<Int>.tick(): List<Int> {
        val n = this.size
        val (maxIndex, maxValue) = this.withIndex().maxBy { it.value }!!
        println("max = ${maxIndex}, $maxValue")
        val minJump = maxValue / n
        return this.withIndex().map { (index, value) ->
            val offset = (((index - maxIndex - 1) % n) + n) % n
            val extra = if (offset < maxValue % n) 1 else 0
            val currentCompensate = if (index == maxIndex) -maxValue else 0
            value + minJump + extra + currentCompensate
        }
    }

    private tailrec fun iterate(seen: Set<List<Int>>, steps: Int, current: List<Int>): Pair<Int, List<Int>> {
        val next = current.tick()
        return if (next in seen) {
            steps + 1 to next
        } else {
            iterate(seen.plusElement(next), steps + 1, next)
        }
    }

    fun part1(list: List<Int>): Int {
        return iterate(emptySet(), 0, list).first
    }

    fun part2(list: List<Int>): Int {
        val (_, loopElement) =  iterate(emptySet(), 0, list)
        return iterate(emptySet(), 0, loopElement).first - 1
    }
}

fun main() {
    println(Day6.part1(Day6.input))
    println(Day6.part2(Day6.input))
}

