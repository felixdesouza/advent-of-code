package aoc2021

import common.readLines

object Day4 {
    fun parseInput(lines: List<String>): Pair<List<Int>, List<List<List<Int>>>> {
        val draw = lines.first().split(",").map { Integer.parseInt(it) }

        val boards = lines.drop(2)
                .map { it.trim() }
                .filter { it.isNotEmpty() }
                .chunked(5)
                .map { it.map { it.split("\\s+".toRegex()).map { Integer.parseInt(it) } } }
        return draw to boards
    }

    val input = readLines("/aoc2021/day4.txt")
            .let { parseInput(it) }

    val testInput = """
            7,4,9,5,11,17,23,2,0,14,21,24,10,16,13,6,15,25,12,22,18,20,8,19,3,26,1
            
            22 13 17 11  0
             8  2 23  4 24
            21  9 14 16  7
             6 10  3 18  5
             1 12 20 15 19
            
             3 15  0  2 22
             9 18 13 17  5
            19  8  7 25 23
            20 11 10 24  4
            14 21 16 12  6
            
            14 21 17 24  4
            10 16 15  9 19
            18  8 23 26 20
            22 11 13  6  5
             2  0 12  3  7
        """.trimIndent().lines()
            .let { parseInput(it) }

    fun part1(input: Pair<List<Int>, List<List<List<Int>>>>): Int {
        val (numbers, boards) = input

        val seen = hashSetOf<Int>()

        for (number in numbers) {
            seen.add(number)

            for (board in boards) {
                for (row in board) {
                    if (seen.containsAll(row)) {
                        val chosen = number
                        println(chosen)
                        val unmarked = countUnmarked(seen, board)
                        println(unmarked)
                        return chosen * unmarked
                    }
                }

                for (i in (board.indices)) {
                    val column = mutableSetOf<Int>()
                    for (j in board.indices) {
                        column.add(board[j][i])
                    }
                    if (seen.containsAll(column)) {
                        val chosen = number
                        val unmarked = countUnmarked(seen, board)
                        return chosen * unmarked
                    }
                }
            }
        }
        throw AssertionError("should not reach here")
    }

    private fun countUnmarked(seen: Set<Int>, board: List<List<Int>>): Int {
        return board.map { it.filter { it !in seen }.sum() }.sum()
    }

    fun part2(input: Pair<List<Int>, List<List<List<Int>>>>): Int {
        val (numbers, boards) = input

        val seen = hashSetOf<Int>()
        val completeBoards = hashSetOf<List<List<Int>>>()
        var newBoards = boards
        for (number in numbers) {
            seen.add(number)

            for (board in newBoards) {
                for (row in board) {
                    if (seen.containsAll(row)) {
                        if (newBoards.size == 1) {
                            return number * countUnmarked(seen, board)
                        }
                        completeBoards.add(board)
                        break
                    }
                }


                for (i in (board.indices)) {
                    val column = mutableSetOf<Int>()
                    for (j in board.indices) {
                        column.add(board[j][i])
                    }
                    if (seen.containsAll(column)) {
                        if (newBoards.size == 1) {
                            return number * countUnmarked(seen, board)
                        }
                        completeBoards.add(board)
                        break
                    }
                }

                newBoards = newBoards.filter { it !in completeBoards }
            }
        }
        throw AssertionError("should not reach here")
    }

    @JvmStatic
    fun main(args: Array<String>) {
        println(part1(testInput))
        println(part1(input))
        println(part2(testInput))
        println(part2(input))
    }
}