package aoc2022

import common.Problem

object Day2 : Problem() {
    val input = rawInput.let { parseInput(it) }
    val testInput = """
        A Y
        B X
        C Z
    """.trimIndent().let { parseInput(it) }

    private fun parseInput(rawInput: String): List<Pair<String, String>> {
        return rawInput.lines().map {
            val (opp, me) = it.split(" ")
            opp to me
        }
    }

    fun part1(pairs: List<Pair<String, String>>): Int {
        return pairs.map { (opp, me) ->
            opp to when (me) {
                "X" -> "A"
                "Y" -> "B"
                "Z" -> "C"
                else -> throw AssertionError()
            }
        }.map { (opp, me) -> score(opp, me) }
                .onEach { println(it) }
                .sum()
    }

    private fun score(opp: String, me: String): Int {
        val roundScore = when {
            opp == me -> 3
            opp == "A" && me == "C" || opp == "B" && me == "A" || opp == "C" && me == "B" -> 0
            else -> 6
        }
        val shapeScore = when (me) {
            "A" -> 1
            "B" -> 2
            "C" -> 3
            else -> throw AssertionError()
        }
        return roundScore + shapeScore
    }

    fun part2(pairs: List<Pair<String, String>>): Int {
        val lookup = mapOf(
                ("A" to "X") to "C",
                ("A" to "Y") to "A",
                ("A" to "Z") to "B",
                ("B" to "X") to "A",
                ("B" to "Y") to "B",
                ("B" to "Z") to "C",
                ("C" to "X") to "B",
                ("C" to "Y") to "C",
                ("C" to "Z") to "A"
        )
        return pairs.map { (opp, command) ->
            score(opp, lookup[opp to command]!!)
        }.sum()
    }

    @JvmStatic
    fun main(args: Array<String>) {
        println(part1(input))
        println(part2(input))
    }
}

