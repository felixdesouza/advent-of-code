package aoc2017

import aoc2017.Day12.parseLine
import aoc2017.Day12.part1
import aoc2017.Day12.part2
import com.google.common.graph.Graph
import com.google.common.graph.GraphBuilder
import com.google.common.graph.Graphs
import common.readLines

object Day12 {

    val input = readLines("/aoc2017/day12.txt").map { it.parseLine() }

    fun String.parseLine(): List<Int> {
        val (_, rest) = split(" <-> ")
        return rest.split(", ").map { it.toInt() }
    }

    fun List<List<Int>>.part1(): Int {
        val builder = asGraph()
        return Graphs.reachableNodes(builder, 0).size
    }

    private fun List<List<Int>>.asGraph(): Graph<Int> {
        val builder = GraphBuilder.undirected().allowsSelfLoops(true).build<Int>()
        return builder.apply {
            withIndex().forEach { (source, adjacents) -> adjacents.forEach { putEdge(source, it) } }
        }
    }

    fun List<List<Int>>.part2(): Int {
        return generateSequence(asGraph()) { curr ->
            if (curr.nodes().isEmpty()) {
                null
            } else {
                val nextNode = curr.nodes().first()
                val nodesInGroup = Graphs.reachableNodes(curr, nextNode)
                println(nodesInGroup)
                val remainingNodes = curr.nodes().toSet() - nodesInGroup
                Graphs.inducedSubgraph(curr, remainingNodes)
            }
        }
                .drop(1)
                .count()
    }
}

fun main() {
    val testInput = """
        0 <-> 2
        1 <-> 1
        2 <-> 0, 3, 4
        3 <-> 2, 4
        4 <-> 2, 3, 6
        5 <-> 6
        6 <-> 4, 5
    """.trimIndent().lines().map { it.parseLine() }
    println(Day12.input.part1())
    println(Day12.input.part2())
}