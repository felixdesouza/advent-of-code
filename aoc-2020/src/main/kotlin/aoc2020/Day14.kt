package aoc2020

import common.openFile
import common.readLines

object Day14 {

    val input = readLines("/aoc2020/day14.txt")

    fun part1(input: List<String>): Long {
        val map: MutableMap<Int, Long> = mutableMapOf()
        var currentMask: Map<Int, Int> = emptyMap()

        for (line in input) {
            when {
                line.startsWith("mask = ") -> {
                    val mask = line.slice(7 until line.length)
                    currentMask = mask.reversed().withIndex().filterNot { it.value == 'X' }.map { (index, value) -> index to value.toInt() - '0'.toInt() }.toMap()
                    println("mask: $currentMask")
                }
                line.startsWith("mem") -> {
                    val address = line.slice(line.indexOf('[') + 1 until line.indexOf(']')).toInt()
                    val value = line.slice(line.indexOf('=') + 2 until line.length).toLong()

                    val ones = currentMask.filterValues { it == 1 }.map { 1L shl it.key }.fold(0L) { a, b -> a.or(b) }
                    val zeros = currentMask.filterValues { it == 0 }.map { 1L shl it.key }.fold(0L) {a, b -> a.or(b)}

                    println(address)
                    println(value)

                    val maskedValue = value.or(ones).and(zeros.inv())
//                    println(Integer.toBinaryString(maskedValue))
                    map[address] = maskedValue
                }
            }
        }

        return map.values.sum()
    }
}

fun main() {
    val testInput = """
        mask = XXXXXXXXXXXXXXXXXXXXXXXXXXXXX1XXXX0X
        mem[8] = 11
        mem[7] = 101
        mem[8] = 0
    """.trimIndent().lines()

    println(Day14.part1(Day14.input))
}