package aoc2021

import com.google.common.collect.HashMultiset
import com.google.common.collect.Sets
import common.Coordinate
import common.Coordinate3d
import common.readLines

object Day19 {

    val input = readLines("/aoc2021/day19.txt").let { parse(it) }

    val testInput = readLines("/aoc2021/day19test.txt").let { parse(it) }

    fun parse(input: List<String>): List<Scanner> {
        return input.withIndex().filter { it.value.startsWith("---") }
                .windowed(2, partialWindows = true)
                .map {
                    if (it.size == 1) {
                        input.subList(it.first().index + 1, input.size)
                    } else {
                        val (a, b) = it
                        input.subList(a.index + 1, b.index)
                    }
                }
                .map { it.filter { it.isNotBlank() } }
                .map {
                    it.map {
                        val (x, y, z) = it.split(",").map { Integer.parseInt(it) }
                        Coordinate3d(x, y, z)
                    }
                }
                .withIndex()
                .map { Scanner(id = it.index, pointing = Direction(Axis.Y, true), beacons = it.value) }
    }

    data class Scanner(val id: Int, val pointing: Direction, val beacons: List<Coordinate3d>) {
        companion object {
            val pointingRotations = listOf(Axis.X, Axis.Y, Axis.Z, Axis.X, Axis.Y)
        }

        fun rotatePointingAt(about: Axis): Scanner {
            val newPointing = pointing.rotateRightOnce(about)
            return Scanner(id, newPointing, beacons.map { it.rotate(about) })
        }

        fun rotateAroundPointing(): Scanner {
            return copy(beacons = beacons.map { it.rotate(pointing.axis) })
        }

        fun allOrientations(): List<Scanner> {
            val pointed = pointingRotations.fold(listOf(this) to this) { (scanners, scanner), axis ->
                val newScanner = scanner.rotatePointingAt(axis)
                (scanners + newScanner) to newScanner
            }.first

            return pointed.flatMap { scanner ->
                generateSequence(scanner) { it.rotateAroundPointing() }
                        .take(4).toList()
            }
        }
    }

    enum class Axis { X, Y, Z }

    data class Direction(val axis: Axis, val positive: Boolean) {
        fun rotateRightOnce(about: Axis): Direction {
            return when (axis to about) {
                Axis.X to Axis.X -> this
                Axis.X to Axis.Y -> Direction(Axis.Z, !positive)
                Axis.X to Axis.Z -> Direction(Axis.Y, !positive)

                Axis.Y to Axis.X -> Direction(Axis.Z, positive)
                Axis.Y to Axis.Y -> this
                Axis.Y to Axis.Z -> Direction(Axis.X, positive)

                Axis.Z to Axis.X -> Direction(Axis.Y, !positive)
                Axis.Z to Axis.Y -> Direction(Axis.X, positive)
                Axis.Z to Axis.Z -> this
                else -> throw AssertionError("should not e here")
            }
        }
    }

    data class ResolvedScanner(val scanner: Scanner, val coordinate: Coordinate3d)

    fun Coordinate.rotate90() = Coordinate(this.y, -this.x)
    fun Coordinate.rotate90(n: Int): Coordinate {
        var coord = this
        for (i in 0 until n) {
            coord = coord.rotate90()
        }
        return coord
    }

    private fun Coordinate3d.project(axis: Axis) = when (axis) {
        Axis.X -> Coordinate(-y, z)
        Axis.Y -> Coordinate(x, z)
        Axis.Z -> Coordinate(x, y)
    }

    private fun Coordinate.explode(axis: Axis, coordinate3d: Coordinate3d) = when (axis) {
        Axis.X -> coordinate3d.copy(y = -x, z = y)
        Axis.Y -> coordinate3d.copy(x = x, z = y)
        Axis.Z -> coordinate3d.copy(x = x, y = y)
    }

    private fun Coordinate3d.rotate(about: Axis): Coordinate3d {
        return project(about).rotate90(1).explode(about, this)
    }

    private fun resolveScanners(input: List<Scanner>): List<ResolvedScanner> {
        var resolvedScanners = listOf(ResolvedScanner(input[0], Coordinate3d.origin))
        var rest = input.subList(1, input.size)

        while (rest.isNotEmpty()) {
            val newStuff = rest.associateWith { checkCompatBase(resolvedScanners, it) }
                    .filterValues { it != null }
                    .mapValues { it.value!! }
                    .toSortedMap(compareBy { it.id })
                    .onEach { println("${it.key.id}: ${it.value.coordinate}") }

            val newResolvedScanners = resolvedScanners + newStuff.map { it.value }
            val newRest = rest - newStuff.map { it.key }.toSet()
            resolvedScanners = newResolvedScanners
            rest = newRest
        }
        return resolvedScanners
    }

    fun checkCompatBase(resolvedScanners: List<ResolvedScanner>, baseScanner: Scanner): ResolvedScanner? {
        val allOrientations = baseScanner.allOrientations()
        val points = Sets.cartesianProduct(resolvedScanners.toSet(), allOrientations.toSet())
                .associateWith { (a, b) -> checkCompat(a as ResolvedScanner, b as Scanner) }
                .filterValues { it != null }
                .mapValues { it.value!! }

        return if (points.isEmpty()) null else {
            val (key, coordinate) = points.entries.first()
            val orientation = key[1] as Scanner

            val adjustedBeacons = orientation.copy(beacons = orientation.beacons.map { it + coordinate })
            ResolvedScanner(adjustedBeacons, coordinate)
        }
    }

    fun checkCompat(a: ResolvedScanner, b: Scanner): Coordinate3d? {
        val multiset = HashMultiset.create<Coordinate3d>()

        val negatives = b.beacons.map { it.copy(-it.x, -it.y, -it.z) }
        for (negative in negatives) {
            for (beacon in a.scanner.beacons) {
                multiset.add(beacon + negative)
            }
        }

        val candidates = multiset.entrySet().filter { it.count >= 12 }
        return if (candidates.isNotEmpty()) candidates.first().element else null
    }

    fun parts(input: List<Scanner>) {
        val resolvedScanners = resolveScanners(input)

        resolvedScanners.flatMap { it.scanner.beacons }.toSet().count().let { println(it) }
        Sets.combinations(resolvedScanners.map { it.coordinate }.toSet(), 2)
                .map { it.toList() }
                .map { (a, b) -> a.distance(b) }
                .max()
                .also { println(it) }
    }

    @JvmStatic
    fun main(args: Array<String>) {
        parts(testInput)
        parts(input)
    }

}
