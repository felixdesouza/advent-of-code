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
        val match = regex.find(line)!!
        val (name, weight, _, rawDeps) = match.destructured

        return Program(name, weight.toInt(), rawDeps.split(", ").filter { it.isNotBlank() })
    }

    fun part1(input: List<Program>): String {
        val allDeps = input.flatMap { it.deps }.toSet()
        return input.find { it.name !in  allDeps }!!.name
    }

    fun part2(input: List<Program>): Int {
        val programsByName = input.associateBy { it.name }

        fun compute(sums: Map<Program, Int>, curr: Program): Map<Program, Int> {
            val updatedSums = curr.deps.fold(sums) { currentSums, dep ->
                val program = programsByName[dep]
                compute(currentSums, program!!)
            }
            println("updatedSums = ${updatedSums}")
            return updatedSums.plus(curr to (curr.weight + curr.deps.sumBy { updatedSums[programsByName[it]!!]!! }))
        }

        val base = programsByName[part1(input)]!!
        val sums = compute(emptyMap(), base).onEach { println(it) }

        tailrec fun findDeepestUnbalanced(sums: Map<Program, Int>, curr: Program): Program {
            // find deepest unbalanced
            val childSums = curr.deps.associateWith { sums[programsByName[it]!!]!! }
            val unbalancedChild = childSums.entries.groupBy({ it.value }, { it.key }).entries.firstOrNull { it.value.size == 1 }?.value?.firstOrNull()

            return if (unbalancedChild == null) {
                curr
            } else {
                findDeepestUnbalanced(sums, programsByName[unbalancedChild]!!)
            }
        }

        fun ancestors(ancestors: Map<Program, Program>, curr: Program): Map<Program, Program> {
            return curr.deps.map { programsByName[it]!! to curr }.toMap() + curr.deps.fold(ancestors) { curr, next -> ancestors(curr, programsByName[next]!!)}
        }

        val deepestUnbalanced = findDeepestUnbalanced(sums, base)
        val ancestors = ancestors(emptyMap(), base)

        ancestors.map { (k, v) -> k.name to v.name }.onEach { (a, b) -> println("$a -> $b") }

        val parentOfUnbalanced = ancestors[deepestUnbalanced]!!
        val sibling = parentOfUnbalanced.deps.first { it != deepestUnbalanced.name }.let { programsByName[it]!! }

        return sums[sibling]!! - sums[deepestUnbalanced]!! + deepestUnbalanced.weight
    }
}

fun main() {
    println(Day7.part1(Day7.input))
    println(Day7.part2(Day7.input))


}