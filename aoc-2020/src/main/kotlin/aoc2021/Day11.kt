package aoc2021

import com.google.common.collect.Queues
import common.Coordinate
import common.Grid

object Day11 {

    val input = Grid.parseFile("/aoc2021/day11.txt") { it - '0'}

    val testInput = """
        5483143223
        2745854711
        5264556173
        6141336146
        6357385478
        4167524645
        2176841721
        6882881134
        4846848554
        5283751526
    """.trimIndent()
            .let { Grid.Companion.parse(it) { it - '0' } }

    val testInput2 = """
        11111
        19991
        19191
        19991
        11111
    """.trimIndent()
            .let { Grid.Companion.parse(it) { it - '0' } }

    fun part1(input: Grid<Int>): Int {
        return generateSequence(input) { cur -> cur.runStep() }
                .drop(1)
                .take(100)
                .sumBy { it.countFlash() }
    }

    fun Grid<Int>.runStep(): Grid<Int> {
        val flashSet = hashSetOf<Coordinate>()
        val incremented = this.mapGrid { _, i -> i + 1 }
        val seed = incremented.filter { _, i -> i > 9 }

        flashSet.addAll(seed.keys)

        val queue = Queues.newArrayDeque<Coordinate>(seed.keys)

        while (queue.isNotEmpty()) {
            val coordinate = queue.pop()

            val neighbours = incremented.filter { c, _ -> c in coordinate.allNeighbours() }
            neighbours.forEach {
                incremented[it.key] = it.value + 1
                if (it.value + 1 > 9 && flashSet.add(it.key)) {
                    queue.add(it.key)
                }
            }
        }

        flashSet.forEach { incremented[it] = 0 }
        return incremented
    }

    fun Grid<Int>.countFlash(): Int {
        return this.filter { _, i -> i == 0 }.count()
    }

    fun part2(input: Grid<Int>): Long {
        val requiredFlashes = input.numRows * input.numColumns

        var current = input
        var rounds = 0L
        while (current.countFlash() != requiredFlashes) {
            current = current.runStep()
            rounds += 1
        }
        return rounds
    }

    @JvmStatic
    fun main(args: Array<String>) {
        println(part1(testInput))
        println(part1(input))
        println(part2(testInput))
        println(part2(input))
    }
}
