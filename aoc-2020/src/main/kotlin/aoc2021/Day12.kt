package aoc2021

import com.google.common.graph.Graph
import com.google.common.graph.GraphBuilder
import common.readLines

object Day12 {

    val input = readLines("/aoc2021/day12.txt").let { parseInput(it) }

    val testInput = """
        start-A
        start-b
        A-c
        A-b
        b-d
        A-end
        b-end
    """.trimIndent()
            .lines()
            .let { parseInput(it) }

    val testInput2 = """
        dc-end
        HN-start
        start-kj
        dc-start
        dc-HN
        LN-dc
        HN-end
        kj-sa
        kj-HN
        kj-dc
    """.trimIndent()
            .lines()
            .let { parseInput(it) }

    val testInput3 = """
        fs-end
        he-DX
        fs-he
        start-DX
        pj-DX
        end-zg
        zg-sl
        zg-pj
        pj-he
        RW-he
        fs-DX
        pj-RW
        zg-RW
        start-pj
        he-WI
        zg-he
        pj-fs
        start-RW
    """.trimIndent()
            .lines()
            .let { parseInput(it) }

    fun parseInput(input: List<String>): Graph<Endpoint> {
        val graph = GraphBuilder.undirected().build<Endpoint>()
        input.map { it.split("-") }
                .forEach { (from, to) ->
                    graph.putEdge(Endpoint.parse(from), Endpoint.parse(to))
                }
        return graph
    }

    data class Endpoint(val name: String, val bigCave: Boolean) {

        companion object {
            val start = Endpoint("start", false)
            val end = Endpoint("end", false)
            fun parse(input: String) = Endpoint(input, input.toUpperCase() == input)
        }
    }

    fun part1(input: Graph<Endpoint>): Int {
        return input.countPaths(Endpoint.start, setOf(Endpoint.start), listOf(Endpoint.start.name))
    }

    private fun Graph<Endpoint>.countPaths(startNode: Endpoint, smallSeen: Set<Endpoint>, path: List<String>): Int {
        if (startNode == Endpoint.end) {
            return 1
        }

        val nextNodes = this.adjacentNodes(startNode).filterNot { it in smallSeen }
        if (nextNodes.isEmpty()) {
            return 0
        }

        return nextNodes.sumBy {
            if (!it.bigCave) {
                this.countPaths(it, smallSeen + it, path + it.name)
            } else {
                this.countPaths(it, smallSeen, path + it.name)
            }
        }
    }

    private fun Graph<Endpoint>.countPaths2(startNode: Endpoint, smallSeen: Set<Endpoint>, path: List<String>, smallCaveTwice: Endpoint?): Int {
        if (startNode == Endpoint.end) {
            return 1
        }

        val nextNodes = this.adjacentNodes(startNode)
        if (nextNodes.isEmpty()) {
            return 0
        }

        return nextNodes.sumBy {
            if (!it.bigCave) {
                when {
                    smallCaveTwice != null && it in smallSeen -> 0
                    smallCaveTwice != null && it !in smallSeen ->
                        this.countPaths2(it, smallSeen + it, path + it.name, smallCaveTwice)
                    smallCaveTwice == null && it in smallSeen && it !in setOf(Endpoint.start, Endpoint.end) ->
                        this.countPaths2(it, smallSeen, path + it.name, it)
                    smallCaveTwice == null && it !in smallSeen ->
                        this.countPaths2(it, smallSeen + it, path + it.name, smallCaveTwice)
                    smallCaveTwice == null && it in smallSeen && it in setOf(Endpoint.start, Endpoint.end) ->
                        0
                    else -> throw AssertionError("should not reach")

                }
            } else {
                this.countPaths2(it, smallSeen, path + it.name, smallCaveTwice)
            }
        }
    }

    fun part2(input: Graph<Endpoint>): Int {
        return input.countPaths2(Endpoint.start, setOf(Endpoint.start), listOf(Endpoint.start.name), null)
    }

    @JvmStatic
    fun main(args: Array<String>) {
        println(part1(testInput))
        println(part1(testInput2))
        println(part1(testInput3))
        println(part1(input))
        println(part2(testInput))
        println(part2(testInput2))
        println(part2(testInput3))
        println(part2(input))
    }
}
