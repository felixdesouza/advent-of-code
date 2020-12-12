package common

import com.google.common.collect.Queues
import com.google.common.graph.Graph
import com.google.common.graph.ValueGraph
import com.google.common.graph.ValueGraphBuilder
import io.vavr.kotlin.toVavrList
import java.io.File
import kotlin.math.abs
import kotlin.math.min

fun readLines(path: String): List<String> {
    val file = openFile(path)
    return file.lines()
}

fun openFile(path: String): String {
    val resourcePath = object {}
        .javaClass
        .getResource(path)
        .toURI()

    println(resourcePath)

    return File(resourcePath).readText().trimEnd()
}

data class Vector(val direction: String, val distance: Int)
data class Coordinate(val x: Int, val y: Int) {
    companion object {
        val origin = Coordinate(0, 0)

        fun boundingBox(coordinates: Set<Coordinate>): Pair<Coordinate, Coordinate> {
            val sortedByX = coordinates.sortedBy { it.x }
            val sortedByY = coordinates.sortedBy { it.y }

            return Coordinate(sortedByX.first().x, sortedByY.first().y) to Coordinate(
                sortedByX.last().x,
                sortedByY.last().y
            )
        }
    }

    fun next(vector: Vector): Coordinate {
        return when (vector.direction) {
            "L" -> Coordinate(this.x - vector.distance, this.y)
            "R" -> Coordinate(this.x + vector.distance, this.y)
            "U" -> Coordinate(this.x, this.y + vector.distance)
            "D" -> Coordinate(this.x, this.y - vector.distance)
            else -> throw AssertionError()
        }
    }

    fun absoluteDistance(other: Coordinate): Int {
        return when {
            other.x == this.x -> abs(other.y - this.y)
            other.y == this.y -> abs(other.x - this.x)
            else -> throw UnsupportedOperationException("cannot get to other coordinate in a straight line")
        }
    }

    fun neighbours(): List<Coordinate> {
        return listOf(
            this.copy(x = x - 1),
            this.copy(x = x + 1),
            this.copy(y = y - 1),
            this.copy(y = y + 1))
    }

    fun diagonalNeighbours(): List<Coordinate> {
        return listOf(
            this.copy(x = x - 1, y = y - 1),
            this.copy(x = x - 1, y = y + 1),
            this.copy(x = x + 1, y = y - 1),
            this.copy(x = x + 1, y = y + 1))
    }

    fun allNeighbours(): List<Coordinate> {
        return neighbours().plus(diagonalNeighbours())
    }

    fun plus(coordinate: Coordinate): Coordinate {
        return Coordinate(this.x + coordinate.x, this.y + coordinate.y)
    }
}

fun gcd(a: Int, b: Int): Int = gcd(a.toLong(), b.toLong()).toInt()
tailrec fun gcd(a: Long, b: Long): Long {
    return when (b) {
        0L -> a
        else -> gcd(b, a % b)
    }
}

fun lcm(a: Long, b: Long): Long = a / gcd(a, b) * b

fun digits(number: Long) : List<Int> {
    tailrec fun helper(acc: Sequence<Int>, remaining: Long): Sequence<Int> {
        if (remaining == 0L) {
            return acc;
        }

        val sequence = sequence {
            yield((remaining % 10).toInt())
            yieldAll(acc)
        }
        return helper(sequence, remaining / 10)
    }

    return helper(sequenceOf(), number).toList()
}

fun <T> Sequence<T>.repeat() = sequence { while (true) yieldAll(this@repeat) }

inline fun <T, R> Sequence<T>.scanLeft(initial: R, operation: (last: R, T) -> R): List<R> {
    return this.fold(listOf(initial).toVavrList(), { listAcc, next ->
        listAcc.push(operation(listAcc.head(), next))!!
    }).toMutableList().asReversed()
}

fun <T> dijkstra(graph: Graph<T>, startingNode: T, validate: Boolean = true): Map<T, Int> {
    if (graph is ValueGraph<*, *>) {
        return dijkstra(graph as ValueGraph<T, Int>, startingNode, validate)
    }

    val valueGraph = if (graph.isDirected) ValueGraphBuilder.directed().build<T, Int>() else ValueGraphBuilder.undirected().build<T, Int>()
    graph.edges().forEach { valueGraph.putEdgeValue(it.nodeU(), it.nodeV(), 1) }
    return dijkstra(valueGraph, startingNode, validate)
}

fun <T> dijkstra(graph: ValueGraph<T, Int>, startingNode: T, validate: Boolean = true): Map<T, Int> {
    val distances = graph.nodes().associateWith { if (startingNode == it) 0 else Int.MAX_VALUE }.toMutableMap()

    val queue = Queues.newArrayDeque<T>(setOf(startingNode))
    val visited = mutableSetOf<T>()
    while (queue.isNotEmpty()) {
        val current = queue.remove()
        if (current in visited) continue
        visited.add(current)

        for (successor in graph.successors(current)) {
            val alternativeDistance = distances[current]!! + graph.edgeValueOrDefault(current, successor, Int.MAX_VALUE)!!
            distances[successor] = min(distances[successor]!!, alternativeDistance)
        }

        queue.addAll(graph.successors(current))
    }

    if (validate && (graph.nodes() - visited).isNotEmpty()) throw UnsupportedOperationException()

    return distances
}

data class Grid<T>(val grid: Map<Coordinate, T>, val numRows: Int, val numColumns: Int) {

    companion object {
        fun parse(input: String): Grid<Char> {
            return parse(input) {it}
        }

        fun <T> parseFile(fileName: String, f: (char: Char) -> T): Grid<T> {
            return parse(openFile(fileName), f)
        }

        fun <T> parse(input: String, f: (char: Char) -> T): Grid<T> {
            val lines = input.lines()
            val numRows = lines.size
            val numColumns = lines.first().length
            val grid = lines.withIndex()
                .map { (row, text) -> text.map(f).withIndex().map { (col, value) -> Coordinate(col, row) to value } }
                .flatten()
                .toMap()
            return Grid(grid, numRows, numColumns)
        }
    }

    fun print() {
        for (y in (0 until numRows)) {
            for (x in (0 until numColumns)) {
                print(grid[Coordinate(x, y)])
            }
            println()
        }
    }

    fun <R> mapGrid(f: (Coordinate, T) -> R): Grid<R> {
        return Grid(grid.mapValues { (coordinate, value) -> f(coordinate, value) }, numRows, numColumns)
    }

    operator fun get(key: Coordinate): T? {
        return grid[key]
    }
}