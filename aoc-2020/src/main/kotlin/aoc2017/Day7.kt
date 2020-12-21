package aoc2017

import common.readLines

object Day7 {

    private val regex = "([a-z]+) \\((\\d+)\\)( -> (.*))?".toRegex()

    val testInput = """
        pbga (66)
        xhth (57)
        ebii (61)
        havc (66)
        ktlj (57)
        fwft (72) -> ktlj, cntj, xhth
        qoyq (66)
        padx (45) -> pbga, havc, qoyq
        tknk (41) -> ugml, padx, fwft
        jptl (61)
        ugml (68) -> gyxo, ebii, jptl
        gyxo (61)
        cntj (57)
    """.trimIndent().lines().map { parseInput(it) }

    val input = readLines("/aoc2017/day7.txt").map { parseInput(it) }

    data class Program(val name: String, val weight: Int, val deps: List<String>)
    fun parseInput(line: String): Program {
        println(line)
        val match = regex.find(line)!!
        val (name, weight, _, rawDeps) = match.destructured

        return Program(name, weight.toInt(), rawDeps.split(", "))
    }

    fun part1(input: List<Program>): String {
        val allDeps = input.flatMap { it.deps }.toSet()
        return input.find { it.name !in  allDeps }!!.name
    }
}

fun main() {
    println(Day7.part1(Day7.input))

}