package aoc2017

import aoc2017.Day12.part1
import com.google.common.graph.GraphBuilder
import com.google.common.graph.Graphs
import common.readLines

object Day12 {

    val input = readLines("/aoc2017/day12.txt").map { it.parseLine() }

    private fun String.parseLine(): List<Int> {
        val (_, rest) = split(" <-> ")
        return rest.split(", ").map { it.toInt() }
    }

    fun List<List<Int>>.part1(): Int {
        val builder = GraphBuilder.undirected().allowsSelfLoops(true).build<Int>()
        this.withIndex().forEach { (source, adjacents) -> adjacents.forEach { builder.putEdge(source, it) } }
        return Graphs.reachableNodes(builder, 0).size
    }
}

fun main() {
    println(Day12.input.part1())
}