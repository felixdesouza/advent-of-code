package aoc2021

import common.Coordinate
import common.Grid
import common.openFile

object Day20 {

    val input = openFile("/aoc2021/day20.txt").let { Input.parse(it) }

    val testInput = openFile("/aoc2021/day20test.txt").let { Input.parse(it) }

    data class Input(val algo: Set<Int>,
                     val inputImage: Set<Coordinate>,
                     val topLeft: Coordinate,
                     val bottomRight: Coordinate,
                     val infiniteEdgeFilled: Boolean) {
        companion object {
            fun parse(input: String): Input {
                val lines = input.lines()
                val algo = lines.first().withIndex().filter { it.value == '#' }.map { it.index }.toSet()
                val coordinates = Grid.parse(lines.drop(2).joinToString("\n"))
                        .filter { _, c -> c == '#' }.keys

                val (topLeft, bottomRight) = Coordinate.boundingBox(coordinates)
                return Input(algo, coordinates, topLeft, bottomRight, false)
            }
        }

        fun Coordinate.isBoundedBy(topLeft: Coordinate, bottomRight: Coordinate): Boolean {
            return y in topLeft.y .. bottomRight.y && x in topLeft.x .. bottomRight.x
        }

        fun iterate(): Input {
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
//                    if (!coord.isBoundedBy(topLeft, bottomRight)) {
//                        print("$coord: ")
//                    }
                    val algoKey = (coord.allNeighbours() + coord).sortedWith(compareBy({ it.y }, { it.x }))
                            .associateWith {
                                if (it.isBoundedBy(topLeft, bottomRight)) {
                                    if (it in inputImage) "1" else "0"
                                } else {
                                    if (infiniteEdgeFilled) "1" else "0"
                                }
                            }
//                            .onEach { if (!coord.isBoundedBy(topLeft, bottomRight)) print("(${it.key.x}, ${it.key.y}): ${it.value} ") }
                            .map { it.value }
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
            return copy(topLeft = newTopLeft, bottomRight =  newBottomRight, inputImage = newSetPixels, infiniteEdgeFilled = newInfiniteEdge)
        }

        fun print() {
            for (y in topLeft.y..bottomRight.y) {
                for (x in topLeft.x..bottomRight.x) {
                    val char = if (Coordinate(x, y) in inputImage) "#" else "."
                    print(char)
                }
                println()
            }
        }
    }

    fun parts(input: Input) {
        fun iterate(n: Int): Input = generateSequence(input) { it.iterate() }.drop(n).first()
        iterate(2).inputImage.size.also { println("part 1: $it") }
        iterate(50).inputImage.size.also { println("part 2: $it") }

    }

    @JvmStatic
    fun main(args: Array<String>) {
        parts(testInput)
        parts(input)
    }

}
