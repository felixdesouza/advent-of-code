package aoc2020

import common.openFile

object Day18 {

    val input = openFile("/aoc2020/day18.txt").lines().map { it.replace(" ",  "") }

    fun evaluate(line: String): Long {
        var cur = 0L
        var index = 0

        var operation: ((a: Long, b: Long) -> Long)? = null

        fun findMatchingParenthesis(i: Int): Int {
            var count = 1
            var endIndex = i
            while (count != 0) {
                when (line[endIndex]) {
                    '(' -> count += 1
                    ')' -> count -= 1
                    else -> {}
                }
                endIndex += 1
            }

            return endIndex
        }

        fun pushNum(num: Long) {
            if (operation == null) {
                cur = num
            } else {
                cur = operation!!.invoke(cur, num)
                operation = null
            }
            println("cur is now $cur")
        }

        while (index < line.length) {
            val char = line[index]

            println("char: $char")

            when (char) {
                in '1'..'9' -> {
                    val num = (char - '0').toLong()
                    pushNum(num)
                    index += 1
                }
                '+' -> {
                    operation = { a, b -> a + b }
                    println("operation: +")
                    index += 1
                }
                '*' -> {
                    operation = { a, b -> a * b }
                    println("operation: *")
                    index += 1
                }
                '(' -> {
                    val endParenthesis = findMatchingParenthesis(index + 1)
                    val inner = line.slice(index + 1 until endParenthesis - 1)
                    println("---")
                    println("inner: $inner")
                    val parenthesisEvaluated = evaluate(inner)
                    println("evaluated inner: $inner -> $parenthesisEvaluated")
                    println("---")
                    pushNum(parenthesisEvaluated)
                    index = endParenthesis
                }
            }
        }

        return cur
    }

    fun part1(lines: List<String>): Long {
        return lines.map { evaluate(it) }.sum()

    }
}

fun main() {
    println(Day18.part1(Day18.input))
}