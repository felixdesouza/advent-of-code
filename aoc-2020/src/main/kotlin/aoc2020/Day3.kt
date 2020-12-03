package aoc2020

import common.readLines

object Day3 {

    val input = readLines("/aoc2020/day3.txt").let { parseInput(it) }

    private val `part 2 slopes` = listOf((1 to 1), (3 to 1), (5 to 1), (7 to 1), (1 to 2))

    fun parseInput(lines: List<String>) =
        lines.first().length to lines.map { it.withIndex().filter { it.value == '#' }.map { it.index }.toSet() }

    fun part1(input: Pair<Int, List<Set<Int>>>): Int {
        val (length, treesByRow) = input
        return traverse(length, (3 to 1), treesByRow)
    }

    fun part2(input: Pair<Int, List<Set<Int>>>): Long {
        val (length, treesByRow) = input
        return `part 2 slopes`.associateWith { slope -> traverse(length, slope, treesByRow) }
            .onEach { println(it) }
            .values.map { it.toLong() }.reduce { a, b -> a * b }
    }

    private fun traverse(length: Int, slope: Pair<Int, Int>, treesByRow: List<Set<Int>>): Int {
        val (x, y) = slope
        return treesByRow.withIndex().drop(y).filter { (index, _) -> index % y == 0 }
            .count { (index, trees) -> (index / y * x % length) in trees }
    }
}
