package aoc2020

import com.google.common.graph.GraphBuilder
import com.google.common.graph.MutableGraph
import common.openFile
import java.util.regex.Pattern

object Day19 {

    val input = openFile("/aoc2020/day19.txt").let { parseInput(it) }

    data class Input(val resolved: Map<Int, String>, val unresolved: Map<Int, String>, val thignsToCheck: List<String>)

    fun parseInput(raw: String): Input {
        val (rules, input) = raw.split("\n\n")
        val thingsToCheck = input.lines()

        val (resolved, unresolved) = rules.lines().fold(mapOf<Int, String>() to mapOf<Int, String>()) { state, next ->
            val (rule, definition) = next.split(": ")
            val (resolved, unresolved) = state

            val pair = rule.toInt() to definition
            if (definition.contains("\"")) {
                resolved.plus(rule.toInt() to definition.replace("\"", "")) to unresolved
            } else {
                resolved to unresolved.plus(pair)
            }
        }

        return Input(resolved, unresolved, thingsToCheck)

//        val graph = GraphBuilder.directed().build<Int>()
//        unresolved.forEach { (rule, definition) ->
//            definition.split(' ', '|').map { it.toInt() }.forEach { graph.putEdge(it, rule) }
//        }
//
//        val S = graph.nodes().filter { graph.predecessors(it).isEmpty() }


    }

    fun resolve(n: Int, resolved: Map<Int, String>, unresolved: Map<Int, String>): Pair<Map<Int, String>, Map<Int, String>> {
        if (n in resolved) {
            return resolved to unresolved
        }

        val definition = unresolved[n]!!
        val nDeps = definition.replace("|", "").split("\\s+".toRegex()).map { it.toInt() }.distinct()
        println("deps for $n: $nDeps")
        val (newResolved, newUnresolved) = nDeps.fold(resolved to unresolved) { (currResolved, currUnresolved), next ->
            resolve(next, currResolved, currUnresolved)
        }

        val resolvedDefinition = nDeps.fold(definition) { currDefinition, dependency ->
            println("replacing $dependency in $currDefinition")
            currDefinition.replace("\\b$dependency\\b".toRegex(), "(${newResolved[dependency]!!})")
        }.replace(" ", "")

        println("resolved $n -> $resolvedDefinition")

        return newResolved.plus(n to resolvedDefinition) to newUnresolved.minus(n)
    }

    fun part1(input: Input): Int {
        val (resolved, unresolved, thingsToCheck) = input
        val (finalResolved, bla) = resolve(0, resolved, unresolved)

        if (bla.isNotEmpty()) throw AssertionError("missed something")
        val rootResolution = finalResolved[0]!!

        val compile = Pattern.compile("^$rootResolution$")

        return thingsToCheck.count { compile.matcher(it).matches() }
    }


}

fun main() {
    val testInput = """
        0: 4 1 5
        1: 2 3 | 3 2
        2: 4 4 | 5 5
        3: 4 5 | 5 4
        4: "a"
        5: "b"

        ababbb
        bababa
        abbbab
        aaabbb
        aaaabbb
    """.trimIndent().let { Day19.parseInput(it) }

//    println(testInput)

    println(Day19.part1(Day19.input))
}