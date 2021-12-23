package aoc2021

import com.google.common.collect.*
import common.Coordinate3d
import common.openFile

object Day22 {

    val input = openFile("/aoc2021/day22.txt").let { Line.parseLines(it) }
    val testInput2 = openFile("/aoc2021/day22test.txt").let { Line.parseLines(it) }

    val testInput3 = """
        on x=10..12,y=10..12,z=10..12
        on x=11..13,y=11..13,z=11..13
        off x=9..11,y=9..11,z=9..11
        on x=10..10,y=10..10,z=10..10
    """.trimIndent()
            .let { Line.parseLines(it) }

    val testInput = """
        on x=-20..26,y=-36..17,z=-47..7
        on x=-20..33,y=-21..23,z=-26..28
        on x=-22..28,y=-29..23,z=-38..16
        on x=-46..7,y=-6..46,z=-50..-1
        on x=-49..1,y=-3..46,z=-24..28
        on x=2..47,y=-22..22,z=-23..27
        on x=-27..23,y=-28..26,z=-21..29
        on x=-39..5,y=-6..47,z=-3..44
        on x=-30..21,y=-8..43,z=-13..34
        on x=-22..26,y=-27..20,z=-29..19
        off x=-48..-32,y=26..41,z=-47..-37
        on x=-12..35,y=6..50,z=-50..-2
        off x=-48..-32,y=-32..-16,z=-15..-5
        on x=-18..26,y=-33..15,z=-7..46
        off x=-40..-22,y=-38..-28,z=23..41
        on x=-16..35,y=-41..10,z=-47..6
        off x=-32..-23,y=11..30,z=-14..3
        on x=-49..-5,y=-3..45,z=-29..18
        off x=18..30,y=-20..-8,z=-3..13
        on x=-41..9,y=-7..43,z=-33..15
        on x=-54112..-39298,y=-85059..-49293,z=-27449..7877
        on x=967..23432,y=45373..81175,z=27513..53682
    """.trimIndent()
            .let { Line.parseLines(it) }


    data class Line(val xRange: IntRange, val yRange: IntRange, val zRange: IntRange, val isOn: Boolean) {
        companion object {
            fun parseLines(string: String): List<Line> {
                return string.lines().map { parseLine(it) }
            }

            fun parseLine(line: String): Line {
                val (operation, ranges) = line.split(" ")
                val flag = operation == "on"
                val (x, y, z) = ranges.split(",")
                        .map {
                            val (start, finish) = it.drop(2).split("..").map { Integer.parseInt(it) }
                            start..finish
                        }
                return Line(x, y, z, flag)
            }
        }

        fun containsCoordinate(coordinate: Coordinate3d) =
                coordinate.x in xRange && coordinate.y in yRange && coordinate.z in zRange

        fun isWithinInitRegion() = xRange.isWithinInitRegion() && yRange.isWithinInitRegion() && zRange.isWithinInitRegion()
    }

    fun RangeMap<Int, RangeMap<Int, RangeSet<Int>>>.add(line: Line) {
        val yRangeMap = TreeRangeMap.create<Int, RangeSet<Int>>()
        yRangeMap.put(line.yRange.toGuava(), TreeRangeSet.create<Int>(listOf(line.zRange.toGuava())))

        merge(line.xRange.toGuava(), yRangeMap) { old, new ->
            val newYRange = TreeRangeMap.create<Int, RangeSet<Int>>()
            newYRange.putAll(old)

            new.asMapOfRanges().forEach { (yRange, zRange) ->

                newYRange.merge(yRange, zRange) { oldZ, newZ ->
                    val mergedZ = TreeRangeSet.create(oldZ)
                    mergedZ.addAll(newZ)
                    mergedZ
                }
            }
            newYRange
        }
    }

    fun RangeMap<Int, RangeMap<Int, RangeSet<Int>>>.print() {
        asMapOfRanges().forEach { xRange, yMap ->
            yMap.asMapOfRanges().forEach { yRange, zSet ->
                zSet.asRanges().forEach { zRange ->
                    xRange.toKotlin().forEach { x ->
                        yRange.toKotlin().forEach { y ->
                            zRange.toKotlin().forEach { z ->
                                println("$x $y $z")
                            }
                        }
                    }
                }
            }
        }
    }

    fun RangeMap<Int, RangeMap<Int, RangeSet<Int>>>.remove(line: Line) {
        val stagedDeletes = TreeRangeMap.create<Int, RangeMap<Int, RangeSet<Int>>>()
        subRangeMap(line.xRange.toGuava()).asMapOfRanges().forEach { x, yRangeMap ->
            val yRangeMapReplacement = TreeRangeMap.create<Int, RangeSet<Int>>()
            yRangeMap.subRangeMap(line.yRange.toGuava()).asMapOfRanges().forEach { y, zRangeSet ->
                val zRangeSetReplacement = TreeRangeSet.create(zRangeSet)
                zRangeSetReplacement.remove(line.zRange.toGuava())
                zRangeSet.subRangeSet(line.zRange.toGuava()).asRanges().forEach { z ->
                    yRangeMapReplacement.put(y, zRangeSetReplacement)
                }
            }
            stagedDeletes.put(x, yRangeMapReplacement)
        }

        stagedDeletes.asMapOfRanges().forEach { newX, newYRangeMap ->
            this.merge(newX, newYRangeMap) { oldYRangeMap, newYRangeMap ->
                val newYRange = TreeRangeMap.create<Int, RangeSet<Int>>()
                newYRange.putAll(oldYRangeMap)

                newYRangeMap.asMapOfRanges().forEach { yRange, zRangeSet ->
                    newYRange.merge(yRange, zRangeSet) { _, newZ -> newZ }
                }

                newYRange
            }
        }
    }

    fun IntRange.size() = endInclusive - start + 1L
    fun IntRange.isWithinInitRegion() = first in (-50..50) && endInclusive in (-50..50)
    fun IntRange.toGuava() = Range.closed(start, endInclusive)

    fun Range<Int>.size() = toKotlin().size()

    fun Range<Int>.toKotlin(): IntRange {
        val lower = if (lowerBoundType() == BoundType.OPEN) lowerEndpoint() + 1 else lowerEndpoint()
        val upper = if (upperBoundType() == BoundType.OPEN) upperEndpoint() - 1 else upperEndpoint()
        return lower..upper
    }

    fun RangeMap<Int, RangeMap<Int, RangeSet<Int>>>.numberOfLights(): Long {
        return asMapOfRanges().entries.map { (xRange, yRangeSubMap) ->
            val yRangeTotal = yRangeSubMap.asMapOfRanges().entries.map { (yRange, zRangeSet) ->
                val zRangeTotal = zRangeSet.asRanges().map { zRange -> zRange.size() }.sum()
                yRange.size() * zRangeTotal
            }.sum()
            xRange.size() * yRangeTotal
        }.sum()
    }

    fun parts(input: List<Line>) {
        val filteredLines = input.filter { it.isWithinInitRegion() }
        val onLights = (-50..50).flatMap { z ->
            (-50..50).flatMap { y ->
                (-50..50).mapNotNull { x ->
                    val coordinate = Coordinate3d(x, y, z)
                    val flag = filteredLines.asSequence().filter { it.containsCoordinate(coordinate) }
                            .lastOrNull()?.isOn ?: false
                    coordinate.takeIf { flag }
                }
            }
        }
        println("part 1: ${onLights.size}")

        val remainingLines = input
        val state = TreeRangeMap.create<Int, RangeMap<Int, RangeSet<Int>>>()
        state.add(remainingLines.first())
        remainingLines.drop(1).forEach {
            if (it.isOn) {
                state.add(it)
            } else {
                state.remove(it)
            }
        }

        println("part 2: ${state.numberOfLights()}")
    }

    @JvmStatic
    fun main(args: Array<String>) {
        parts(testInput)
        parts(testInput2)
        parts(testInput3)
        parts(input)

    }

}