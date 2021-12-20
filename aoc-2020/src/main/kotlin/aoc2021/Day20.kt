package aoc2021

import common.Coordinate
import common.Grid
import common.iterate
import common.openFile

object Day20 {

    val input = openFile("/aoc2021/day20.txt").let { Input.parse(it) }

    val testInput = openFile("/aoc2021/day20test.txt").let { Input.parse(it) }

    data class Input(val algo: Set<Int>,
                     val inputImage: Set<Coordinate>,
                     val infiniteEdgeFilled: Boolean) {
        companion object {
            fun parse(input: String): Input {
                val lines = input.lines()
                val algo = lines.first().withIndex().filter { it.value == '#' }.map { it.index }.toSet()
                val coordinates = Grid.parse(lines.drop(2).joinToString("\n"))
                        .filter { _, c -> c == '#' }.keys

                return Input(algo, coordinates, false)
            }
        }

        fun iterate(): Input {
            val (topLeft, bottomRight) = Coordinate.boundingBox(inputImage)
            val (newTopLeft, newBottomRight) = topLeft + Coordinate(-1, -1) to bottomRight + Coordinate(1, 1)

            /*
            --------- <- infinite edge i.e. all 1s or all 0s
            --------- <- part of infinite edge but can have differing values
            --#..#.-- <- if on the edge, any neighbour in infinite edge has value of infinite edge
            --#....--
            --##..#--
            --..#..--
            --..###--
            ---------
            ---------
             */
            val newSetPixels = (newTopLeft.y..newBottomRight.y).flatMap { y ->
                (newTopLeft.x..newBottomRight.x).mapNotNull { x ->
                    val coord = Coordinate(x, y)
                    val algoKey = (coord.allNeighbours() + coord).sortedWith(compareBy({ it.y }, { it.x }))
                            .asSequence()
                            .map {
                                if (it.isBoundedBy(topLeft, bottomRight)) {
                                    if (it in inputImage) "1" else "0"
                                } else {
                                    if (infiniteEdgeFilled) "1" else "0"
                                }
                            }
                            .joinToString("")
                            .let { Integer.parseInt(it, 2) }
                    coord.takeIf { algoKey in algo }
                }
            }.toSet()

            val newInfiniteEdge = when {
                !infiniteEdgeFilled && 0 !in algo -> false
                !infiniteEdgeFilled && 0 in algo -> true
                infiniteEdgeFilled && 511 !in algo -> false
                infiniteEdgeFilled && 511 in algo -> true
                else -> throw AssertionError("should not reach")
            }
            return copy(inputImage = newSetPixels, infiniteEdgeFilled = newInfiniteEdge)
        }
    }

    fun parts(input: Input) {
        iterate(2, input) { it.iterate() }.inputImage.size.also { println("part 1: $it") }
        iterate(50, input) { it.iterate() }.inputImage.size.also { println("part 2: $it") }
    }

    @JvmStatic
    fun main(args: Array<String>) {
        parts(testInput)
        parts(input)
    }

}
