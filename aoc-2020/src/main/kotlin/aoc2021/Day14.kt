package aoc2021

import common.readLines
import java.lang.Long.max

object Day14 {

    val input = readLines("/aoc2021/day14.txt").let { Input.parse(it) }

    val testInput = """
        NNCB

        CH -> B
        HH -> N
        CB -> H
        NH -> C
        HB -> C
        HC -> B
        HN -> C
        NN -> C
        BH -> H
        NC -> B
        NB -> B
        BN -> B
        BB -> N
        BC -> B
        CC -> N
        CN -> C
    """.trimIndent()
            .lines()
            .let { Input.parse(it) }

    data class Input(val template: String, val pairInsertions: Map<String, String>) {
        companion object {
            fun parse(input: List<String>): Input {
                val (template, pairInsertions) = input.filter { it.isNotBlank() }.partition { !it.contains("->") }

                return Input(
                        template = template.first(),
                        pairInsertions = pairInsertions.map { it.split(" -> ").let { (left, right) -> left to right } }.toMap())
            }
        }

        fun run(n: Int): Long {
            val pairCounts = template.windowed(2).groupingBy { it }.eachCount().mapValues { (_, value) -> value.toLong() }
            val charCounts = template.groupingBy { it }.eachCount().mapValues { (_, value) -> value.toLong() }
            val initial = Box(pairs = pairCounts, charCounts = charCounts)
            val stepN = generateSequence(initial) { runStep(it) }.drop(n).first()

            val (_, maxCount) = stepN.charCounts.maxBy { it.value }!!
            val (_, minCount) = stepN.charCounts.minBy { it.value }!!

            return maxCount - minCount
        }

        private fun runStep(input: Box): Box {
            val resultPairs = input.pairs.toMutableMap()
            val resultCharCounts = input.charCounts.toMutableMap()
            input.pairs.forEach { (pair, count) ->
                if (pair in pairInsertions) {
                    val mapping = pairInsertions[pair]!!
                    val leftKey = pair[0] + mapping
                    val rightKey = mapping + pair[1]

                    resultPairs[leftKey] = (resultPairs[leftKey] ?: 0) + count
                    resultPairs[rightKey] = (resultPairs[rightKey] ?: 0) + count
                    resultPairs[pair] = max((resultPairs[pair] ?: 0) - count, 0)
                    resultCharCounts[mapping[0]] = (resultCharCounts[mapping[0]] ?: 0) + count
                }
            }

            return Box(pairs = resultPairs, charCounts = resultCharCounts)
        }
    }

    data class Box(val pairs: Map<String, Long>, val charCounts: Map<Char, Long>)

    fun part1(input: Input): Long = input.run(10)
    fun part2(input: Input): Long = input.run(40)

    @JvmStatic
    fun main(args: Array<String>) {
        println(part1(testInput))
        println(part1(input))

        println(part2(testInput))
        println(part2(input))
    }

}
