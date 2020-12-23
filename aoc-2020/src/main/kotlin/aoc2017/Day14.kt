package aoc2017

import aoc2017.Day14.part1
import aoc2017.Day14.part2
import common.Coordinate

object Day14 {
    val input = "stpzcrnm"
    val testInput = "flqrgnkx"

    private fun String.computeSetBitsForHashes(): Set<Coordinate> {
        val input = (0..127).map { "$this-$it" }
        return input.map { Day10.part2(it) }
                .map { it.chunked(8).map { it.toLong(16).toString(2).padStart(32, '0') }.joinToString("") }
                .mapIndexed { y, row -> row.withIndex().filter { it.value == '1' }.map { it.index }.map { x -> Coordinate(x, y) } }
                .flatten()
                .toSet()
    }

    fun String.part1(): Int {
        return computeSetBitsForHashes().count()
    }

    fun String.part2(): Int {
        val allBits = computeSetBitsForHashes()

        fun findBitsInRegion(seen: Set<Coordinate>, current: Coordinate): Set<Coordinate> {
            val newSeen = seen + current
            val validNeighbours = allBits.intersect(current.neighbours()) - seen

            return validNeighbours.fold(newSeen) { state, next -> findBitsInRegion(state, next) }
        }

        val (_, numberOfRegions) = allBits.fold(emptySet<Coordinate>() to 0) { (seen, regions), next ->
            if (next in seen) {
                seen to regions
            } else {
                val everythingInRegion = findBitsInRegion(emptySet(), next)
                (seen + everythingInRegion) to regions + 1
            }
        }

        return numberOfRegions
    }
}

fun main() {
    Day14.input.part1().also { println(it) }
    Day14.input.part2().also { println(it) }
}