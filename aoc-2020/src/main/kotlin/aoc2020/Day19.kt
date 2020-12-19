package aoc2020

import com.google.common.graph.GraphBuilder
import com.google.common.graph.MutableGraph
import common.openFile
import common.repeat
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
        val nDeps = definition.replace("|", "").replace("+", "").split("\\s+".toRegex()).map { it.toInt() }.distinct()

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

    fun part1(input: Input, n: Int = 0): Int {
        val (resolved, unresolved, thingsToCheck) = input
        val (finalResolved, bla) = resolve(n, resolved, unresolved)

        if (bla.isNotEmpty()) throw AssertionError("missed something")
        val rootResolution = finalResolved[n]!!

        val compile = Pattern.compile("^$rootResolution$")

        return thingsToCheck.count { compile.matcher(it).matches() }
    }

    fun part2(input: Input): Int {
        val (_, unresolved, _) = input
        val unrolled = (1..90).map {
            val first = sequenceOf(42).repeat().take(it).joinToString(" ")
            val second = sequenceOf(31).repeat().take(it).joinToString(" ")
            "$first $second"
        }.joinToString(" | ")

        return part1(input.copy(unresolved = unresolved.plus(8 to "42+").plus(11 to unrolled)))
    }


}

fun main() {
    val testInput = """
42: 9 14 | 10 1
9: 14 27 | 1 26
10: 23 14 | 28 1
1: "a"
11: 42 31
5: 1 14 | 15 1
19: 14 1 | 14 14
12: 24 14 | 19 1
16: 15 1 | 14 14
31: 14 17 | 1 13
6: 14 14 | 1 14
2: 1 24 | 14 4
0: 8 11
13: 14 3 | 1 12
15: 1 | 14
17: 14 2 | 1 7
23: 25 1 | 22 14
28: 16 1
4: 1 1
20: 14 14 | 1 15
3: 5 14 | 16 1
27: 1 6 | 14 18
14: "b"
21: 14 1 | 1 14
25: 1 1 | 1 14
22: 14 14
8: 42
26: 14 22 | 1 20
18: 15 15
7: 14 5 | 1 21
24: 14 1

abbbbbabbbaaaababbaabbbbabababbbabbbbbbabaaaa
bbabbbbaabaabba
babbbbaabbbbbabbbbbbaabaaabaaa
aaabbbbbbaaaabaababaabababbabaaabbababababaaa
bbbbbbbaaaabbbbaaabbabaaa
bbbababbbbaaaaaaaabbababaaababaabab
ababaaaaaabaaab
ababaaaaabbbaba
baabbaaaabbaaaababbaababb
abbbbabbbbaaaababbbbbbaaaababb
aaaaabbaabaaaaababaa
aaaabbaaaabbaaa
aaaabbaabbaaaaaaabbbabbbaaabbaabaaa
babaaabbbaaabaababbaabababaaab
aabbbbbaabbbaaaaaabbbbbababaaaaabbaaabba
    """.trimIndent().let { Day19.parseInput(it) }

    println(Day19.part2(Day19.input))
}