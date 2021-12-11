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
            "W" -> Coordinate(this.x - vector.distance, this.y)
            "E" -> Coordinate(this.x + vector.distance, this.y)
            "N" -> Coordinate(this.x, this.y + vector.distance)
            "S" -> Coordinate(this.x, this.y - vector.distance)
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

    operator fun plus(coordinate: Coordinate): Coordinate {
        return Coordinate(this.x + coordinate.x, this.y + coordinate.y)
    }

    operator fun times(multiplier: Int): Coordinate {
        return Coordinate(this.x * multiplier, this.y * multiplier)
    }
}

fun gcd(a: Int, b: Int): Int = gcd(a.toLong(), b.toLong()).toInt()
tailrec fun gcd(a: Long, b: Long): Long {
    return when (b) {
        0L -> a
        else -> gcd(b, a % b)
    }
}

fun crt(congruences: List<Pair<Long, Long>>): Long {
    val n = congruences.map { it.second }.reduce { a, b -> a * b }
    return congruences.fold(0L) { curr, (a_i, n_i) ->
        val y_i = n / n_i
        val z_i = y_i.toBigInteger().modInverse(n_i.toBigInteger()).longValueExact()
        (curr + a_i*y_i*z_i) % n
    }.let { (it + n) % n }
}

fun lcm(a: Long, b: Long): Long = a / gcd(a, b) * b

fun digits(number: Long) : List<Int> {
    tailrec fun helper(acc: Sequence<Int>, remaining: Long): Sequence<Int> {
        if (remaining == 0L) {
            return acc
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

data class Grid<T>(val grid: MutableMap<Coordinate, T>, val numRows: Int, val numColumns: Int) {

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
            val numColumns = lines.map { it.length }.max()!!
            val grid = lines.withIndex()
                .map { (row, text) -> text.map(f).withIndex().map { (col, value) -> Coordinate(col, row) to value } }
                .flatten()
                .toMap()
            return Grid(grid.toMutableMap(), numRows, numColumns)
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
        return Grid(grid.mapValues { (coordinate, value) -> f(coordinate, value) }.toMutableMap(), numRows, numColumns)
    }

    fun <A> fold(initial: A, operation: (acc: A, coordinate: Coordinate, next: T) -> A): A {
        return (0 until numRows).fold(initial) { acc, row ->
            (0 until numColumns).fold(acc) { colAcc, column ->
                val coordinate = Coordinate(column, row)
                val value = grid[coordinate]!!
                operation(colAcc, coordinate, value)
            }
        }
    }

    fun filter(f: (Coordinate, T) -> Boolean): Map<Coordinate, T> {
        return fold(mapOf()) { acc, coord, next ->
            if (f(coord, next)) acc + (coord to next) else acc
        }
    }

    operator fun get(key: Coordinate): T? {
        return grid[key]
    }

    operator fun set(key: Coordinate, value: T) {
        grid[key] = value
    }
}

data class Coordinate3d(val x: Int, val y: Int, val z: Int) {
    companion object {
        val origin = Coordinate3d(0, 0, 0)

        fun boundingBox(coordinates: Set<Coordinate3d>): Pair<Coordinate3d, Coordinate3d> {
            val sortedByX = coordinates.sortedBy { it.x }
            val sortedByY = coordinates.sortedBy { it.y }
            val sortedByZ = coordinates.sortedBy { it.z }

            return Coordinate3d(sortedByX.first().x, sortedByY.first().y, sortedByZ.first().z) to Coordinate3d(
                    sortedByX.last().x,
                    sortedByY.last().y,
                    sortedByZ.last().z
            )
        }
    }

    fun neighbours(): List<Coordinate3d> {
        val coords = mutableListOf<Coordinate3d>()
        for (newZ in (z-1..z+1)) {
            for (newY in (y-1..y+1)) {
                for (newX in (x-1..x+1)) {
                    coords.add(Coordinate3d(newX, newY, newZ))
                }
            }
        }

        return coords.minus(this)
    }
}

data class Coordinate4d(val x: Int, val y: Int, val z: Int, val w: Int) {
    companion object {
        val origin = Coordinate4d(0, 0, 0, 0)

        fun boundingBox(coordinates: Set<Coordinate4d>): Pair<Coordinate4d, Coordinate4d> {
            val sortedByX = coordinates.sortedBy { it.x }
            val sortedByY = coordinates.sortedBy { it.y }
            val sortedByZ = coordinates.sortedBy { it.z }
            val sortedByW = coordinates.sortedBy { it.w}

            return Coordinate4d(sortedByX.first().x, sortedByY.first().y, sortedByZ.first().z, sortedByW.first().w) to Coordinate4d(
                    sortedByX.last().x,
                    sortedByY.last().y,
                    sortedByZ.last().z,
                    sortedByW.last().w
            )
        }
    }

    fun neighbours(): List<Coordinate4d> {
        val coords = mutableListOf<Coordinate4d>()
        for (newW in (w-1..w+1)) {
            for (newZ in (z - 1..z + 1)) {
                for (newY in (y - 1..y + 1)) {
                    for (newX in (x - 1..x + 1)) {
                        coords.add(Coordinate4d(newX, newY, newZ, newW))
                    }
                }
            }
        }

        return coords.minus(this)
    }
}

fun Int.toBinaryString(numDigits: Int = 0): String = toLong().toBinaryString(numDigits)
fun Long.toBinaryString(numDigits: Int = 0): String = java.lang.Long.toBinaryString(this).padStart(numDigits, '0')

//data class Cube<T>(val grid: Map<Coordinate3d, T>) {
//
//    companion object {
//        fun parse(input: String): Cube<Char> {
//            return parse(input) {it}
//        }
//
//        fun <T> parseFile(fileName: String, f: (char: Char) -> T): Cube<T> {
//            return parse(openFile(fileName), f)
//        }
//
//        fun <T> parse(input: String, f: (char: Char) -> T): Cube<T> {
//            val lines = input.lines()
//            val numRows = lines.size
//            val numColumns = lines.first().length
//            val grid = lines.withIndex()
//                    .map { (row, text) -> text.map(f).withIndex().map { (col, value) -> Coordinate(col, row) to value } }
//                    .flatten()
//                    .toMap()
//            return Cube(grid)
//        }
//    }
//
////    fun print() {
////        for (y in (0 until numRows)) {
////            for (x in (0 until numColumns)) {
////                print(grid[Coordinate(x, y)])
////            }
////            println()
////        }
////    }
//
//    fun <R> mapCube(f: (Coordinate, T) -> R): Grid<R> {
//        return Grid(grid.mapValues { (coordinate, value) -> f(coordinate, value) }, numRows, numColumns)
//    }
//
//    operator fun get(key: Coordinate): T? {
//        return grid[key]
//    }
//}