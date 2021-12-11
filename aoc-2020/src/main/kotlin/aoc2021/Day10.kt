package aoc2021

import common.openFile
import java.util.*

object Day10 {

    val input = openFile("/aoc2021/day10.txt")
            .lines()

    val testInput = """
        [({(<(())[]>[[{[]{<()<>>
        [(()[<>])]({[<{<<[]>>(
        {([(<{}[<>[]}>{[]{[(<()>
        (((({<>}<{<{<>}{[]{[]{}
        [[<[([]))<([[{}[[()]]]
        [{[{({}]{}}([{[{{{}}([]
        {<[[]]>}<{[{[{[]{()[[[]
        [<(<(<(<{}))><([]([]()
        <{([([[(<>()){}]>(<<{{
        <{([{{}}[<[[[<>{}]]]>[]]
    """.trimIndent()
            .lines()

    fun part1(input: List<String>): Int {
        return input.map { checkCorruptedLine(it) }
                .mapNotNull { it.first }
                .map { it.score() }
                .sum()
    }

    private fun Char.opposite(): Char {
        return when (this) {
            '(' -> ')'
            '[' -> ']'
            '{' -> '}'
            '<' -> '>'
            ')' -> '('
            ']' -> '['
            '}' -> '{'
            '>' -> '<'
            else -> this
        }
    }

    private fun Char.score(): Int {
        return when(this) {
            ')' -> 3
            ']' -> 57
            '}' -> 1197
            '>' -> 25137
            else -> 0
        }
    }

    private fun Char.completionScore(): Int {
        return when(this) {
            ')' -> 1
            ']' -> 2
            '}' -> 3
            '>' -> 4
            else -> 0
        }
    }

    private fun checkCorruptedLine(line: String): Pair<Char?, Stack<Char>?> {
        val stack = Stack<Char>()

        for (char in line) {
            when(char) {
                '(', '[', '{', '<' -> {
                    stack.push(char)
                }
                ')', ']', '}', '>' ->
                    if (stack.peek().opposite() == char) {
                        stack.pop()
                    } else {
                        return char to null
                    }
            }
        }

        return null to stack
    }

    fun part2(input: List<String>): Long {
        val scores = input.map { checkCorruptedLine(it) }
                .mapNotNull { it.second }
                .map { it.reversed().map { it.opposite().completionScore() }}
                .map { it.fold(0L) { acc, nextScore -> (acc * 5) + nextScore } }
                .sorted()
        println(scores)
        return scores[scores.size / 2]
    }

    @JvmStatic
    fun main(args: Array<String>) {
        println(part1(testInput))
        println(part1(input))
        println(part2(testInput))
        println(part2(input))
    }
}
