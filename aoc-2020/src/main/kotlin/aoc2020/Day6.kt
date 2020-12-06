package aoc2020

import common.openFile

object Day6 {

    val input = openFile("/aoc2020/day6.txt")

    fun part1(boardingGroups: List<String>): Int {
        return boardingGroups.map { processBoardingGroup(it) }.sum()
    }

    fun part2(boardingGroups: List<String>): Int {
        return boardingGroups.map { processBoardingGroupPart2(it) }.sum()
    }

    fun parseInput(raw: String): List<String> {
        return raw.split("\n\n")
    }

    private fun processBoardingGroup(boardingGroup: String): Int {
        return boardingGroup.replace("\n", "").groupBy { it }
            .count()
    }

    private fun processBoardingGroupPart2(boardingGroup: String): Int {
        val numberOfPassengers = boardingGroup.lines().size
        return boardingGroup.replace("\n", "").groupingBy { it }
            .eachCount()
            .filter { (_, count) -> count == numberOfPassengers }
            .count()
    }
}