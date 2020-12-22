package aoc2017

import common.openFile

object Day9 {

    val input = openFile("/aoc2017/day9.txt")

    fun filterGarbage(line: String): String {
        val removeIgnore = line.replace("!.".toRegex(), "")
                .replace("<[^>]*>".toRegex(), "")
        if ('<' in removeIgnore) throw AssertionError("not cleared garbage")
        return removeIgnore
    }

    fun part1(input: String): Int {
        val pure = filterGarbage(input)
        println(pure)
        val (score, _) = pure.fold(0 to 0) { (score, level), next ->
            println("$score $level | $next")
            when (next) {
                '{' ->  score to level + 1
                '}' -> (score + level) to level - 1
                else -> score to level
            }
        }

        return score
    }
}

fun main() {
    Day9.part1(Day9.input).let { println(it) }
}