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
                }
                line.startsWith("mem") -> {
                    val address = line.slice(line.indexOf('[') + 1 until line.indexOf(']')).toInt()
                    val value = line.slice(line.indexOf('=') + 2 until line.length).toLong()

                    val ones = currentMask.filterValues { it == 1 }.map { 1L shl it.key }.fold(0L) { a, b -> a.or(b) }
                    val zeros = currentMask.filterValues { it == 0 }.map { 1L shl it.key }.fold(0L) {a, b -> a.or(b)}

                    val maskedValue = value.or(ones).and(zeros.inv())
                    map[address] = maskedValue
                }
            }
        }

        return map.values.sum()
    }

    fun part2(input: List<String>): Long {
        val map: MutableMap<Long, Long> = mutableMapOf()
        var currentMask: Map<Int, Int> = emptyMap()
        var xs: List<Int> = emptyList()

        for (line in input) {
            when {
                line.startsWith("mask = ") -> {
                    val mask = line.slice(7 until line.length)
                    currentMask = mask.reversed().withIndex().filterNot { it.value == 'X' }.map { (index, value) -> index to value.toInt() - '0'.toInt() }.toMap()
                    xs = mask.reversed().withIndex().filter { it.value == 'X' }.map { it.index }.sorted()
                }
                line.startsWith("mem") -> {
                    val address = line.slice(line.indexOf('[') + 1 until line.indexOf(']')).toLong()
                    val value = line.slice(line.indexOf('=') + 2 until line.length).toLong()

                    val ones = currentMask.filterValues { it == 1 }.map { 1L shl it.key }.fold(0L) { a, b -> a.or(b) }
                    val maskedAddress = address.or(ones)

                    if (xs.isEmpty()) {
                        map[maskedAddress] = value
                    } else {
                        val combinations = Math.pow(2.toDouble(), xs.size.toDouble()).toLong()
                        for (i in 0 until combinations) {
                            val associations = xs.withIndex().associateWith { (index, _) -> (i and (1L shl index) shr index)}.mapKeys { it.key.value }
                            val ones = associations.filterValues { it == 1L }.map { 1L shl it.key }.fold(0L) { a, b -> a.or(b) }
                            val zeros = associations.filterValues { it == 0L }.map { 1L shl it.key }.fold(0L) {a, b -> a.or(b)}

                            val finalMarkedAddress = maskedAddress.or(ones).and(zeros.inv())
                            println("map[$finalMarkedAddress] = $value")
                            map[finalMarkedAddress] = value
                        }
                    }
                }
            }
        }
        return map.values.sum()
    }
}

fun main() {
    val testInput = """
        mask = 000000000000000000000000000000X1001X
        mem[42] = 100
        mask = 00000000000000000000000000000000X0XX
        mem[26] = 1
    """.trimIndent().lines()

    println(Day14.part1(Day14.input))

    println(Day14.part2(Day14.input))
}