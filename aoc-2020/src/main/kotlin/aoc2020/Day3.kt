package aoc2020

import common.readLines

object Day3 {

    val input = readLines("/aoc2020/day3.txt").let { parseInput(it) }

    fun parseInput(lines: List<String>) =
        lines.first().length to lines.map { it.withIndex().filter { it.value == '#' }.map { it.index }.toSet() }

    fun part1(input: Pair<Int, List<Set<Int>>>): Int {
        val (length, treesByRow) = input;
        return treesByRow.withIndex().drop(1).count { (index, trees) -> (index * 3 % length) in trees }
    }
}
