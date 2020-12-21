package aoc2017

import common.readLines

object Day5 {

    val input = readLines("/aoc2017/day5.txt").map { it.toInt() }

    fun part1(list: List<Int>): Int {
        val array = list.toIntArray()

        tailrec fun jump(steps: Int, offset: Int): Int {
            if (offset >= list.size) {
                return steps
            } else {
                val newJump = offset + array[offset]
                array[offset] += 1
                return jump(steps + 1, newJump)
            }
        }

        return jump(0, 0)
    }

}

fun main() {
    println(Day5.part1(Day5.input))
}