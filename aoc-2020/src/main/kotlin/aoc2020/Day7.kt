package aoc2020

import com.google.common.graph.*
import common.readLines
import ru.lanwen.verbalregex.VerbalExpression

object Day7 {
    val lineRegex = VerbalExpression.regex()
        .capt().digit().endCapt()
        .space()
        .capt().word().space().word().endCapt()
        .space().then("bag").maybe("s")
        .build()

    val input = readLines("/aoc2020/day7.txt")
        .let { parseInput(it) }

    fun parseInput(lines: List<String>): List<Triple<String, String, Int>> {
        return lines.mapNotNull { parseLine(it) }
            .flatten()
    }

    fun parseLine(line: String): List<Triple<String, String, Int>>? {
        val (source, contents) = line.split(" bags contain ")
        if (contents == "no other bags.") {
            return null
        }

        return contents.split(", ", ".").filter { it.isNotBlank() }
            .map {
                val count = lineRegex.getText(it, 1).toInt()
                val sink = lineRegex.getText(it, 2)
                Triple(source, sink, count)
            }
    }

    fun part1(input: List<Triple<String, String, Int>>): Int {
        val graph = ValueGraphBuilder.directed().build<String, Int>()

        input.forEach { (source, sink, count) ->
            graph.putEdgeValue(source, sink, count)
        }

        val transposed = Graphs.transpose(graph).asGraph()

        return Graphs.reachableNodes(transposed, "shiny gold")
            .minus("shiny gold")
            .count()
    }

    fun part2(input: List<Triple<String, String, Int>>): Int {
        val graph = ValueGraphBuilder.directed().build<String, Int>()

        input.forEach { (source, sink, count) ->
            graph.putEdgeValue(source, sink, count)
        }

        if (Graphs.hasCycle(graph.asGraph())) {
            throw AssertionError("deal with this")
        }

        fun count(source: String): Int {
            return graph.successors(source)
                .sumBy { sink ->
                    val count = graph.edgeValue(source, sink).get()

                    count + count * count(sink)
                }
        }

        return count("shiny gold")
    }

}