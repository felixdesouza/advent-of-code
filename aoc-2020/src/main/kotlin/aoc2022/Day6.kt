package aoc2022

import common.Problem

object Day6 : Problem() {

    val input = rawInput

    fun part1(input: String): Int {
        return run(input, 4)
    }

    private fun run(input: String, n: Int) = input.windowedSequence(n).indexOfFirst { it.toSet().size == n } + n

    fun part2(input: String): Int {
        return run(input, 14)
    }

    @JvmStatic
    fun main(args: Array<String>) {
        println(part1("mjqjpqmgbljsphdztnvjfqwrcgsmlb"))
        println(part1("bvwbjplbgvbhsrlpgdmjqwftvncz"))
        println(part1("nppdvjthqldpwncqszvftbrmjlhg"))
        println(part1("nznrnfrfntjfmvfwmzdfjlvtqnbhcprsg"))
        println(part1("zcfzfwzzqfrljwzlrfnpqdbhtmscgvjw"))
        println(part1(input))

        println("---")

        println(part2("mjqjpqmgbljsphdztnvjfqwrcgsmlb"))
        println(part2("bvwbjplbgvbhsrlpgdmjqwftvncz"))
        println(part2("nppdvjthqldpwncqszvftbrmjlhg"))
        println(part2("nznrnfrfntjfmvfwmzdfjlvtqnbhcprsg"))
        println(part2("zcfzfwzzqfrljwzlrfnpqdbhtmscgvjw"))
        println(part2(input))
    }
}

