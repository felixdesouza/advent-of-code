package aoc2017

import common.readLines

object Day5 {

    val input = readLines("/aoc2017/day5.txt").map { it.toInt() }

    fun part1(list: List<Int>): Int {
        return jump(list)
    }

    fun part2(list: List<Int>): Int {
        return jump(list, true)
    }

    private fun jump(list: List<Int>, strange: Boolean = false): Int {
        val array = list.toIntArray()

        println("list = [${list}], strange = [${strange}]")

        tailrec fun jumpInner(steps: Int, offset: Int): Int {
            return if (offset >= list.size) {
                steps
            } else {
                val newJump = offset + array[offset]
                if (strange && array[offset] >= 3) array[offset] -= 1 else array[offset] += 1
                jumpInner(steps + 1, newJump)
            }
        }

        return jumpInner(0, 0)
    }

}

fun main() {
    val testInput = listOf(0, 3, 0, 1, -3)
    println(Day5.part2(Day5.input))
}