package aoc2020

import common.readLines
import ru.lanwen.verbalregex.VerbalExpression

object Day2 {
    val regex = VerbalExpression.regex().startOfLine()
        .capture().digit().oneOrMore().endCapture()
        .then("-")
        .capture().digit().oneOrMore().endCapture()
        .oneOrMore().space()
        .capture().wordChar().endCapture()
        .then(":")
        .oneOrMore().space()
        .capture().word().endCapture()
        .build()


    val inputAsLines = readLines("/aoc2020/day2.txt")
    val part1Input = inputAsLines.map { parseLine(it) }.toSet()
    fun part1(passwords: Set<PasswordWithPolicy>): Int = passwords.count { it.isValid }
    val part2Input = inputAsLines.map { parseLineIntoIndexBasedPolicy(it) }.toSet()
    fun part2(passwords: Set<PasswordWithIndexBasedPolicy>): Int = passwords.count { it.isValid }

    fun parseLine(rawLine: String): PasswordWithPolicy {
        return PasswordWithPolicy(
            (regex.getText(rawLine, 1).toInt()..regex.getText(rawLine, 2).toInt()),
            regex.getText(rawLine, 3).first(), regex.getText(rawLine, 4)
        )
    }

    fun parseLineIntoIndexBasedPolicy(rawLine: String):PasswordWithIndexBasedPolicy {
        return PasswordWithIndexBasedPolicy(
            regex.getText(rawLine, 1).toInt(), regex.getText(rawLine, 2).toInt(),
            regex.getText(rawLine, 3).first(), regex.getText(rawLine, 4)
        )
    }

    data class PasswordWithPolicy(val range: IntRange, val letterRequirement: Char, val password: String) {
        val isValid = password.count { it == letterRequirement } in range
    }

    data class PasswordWithIndexBasedPolicy(val position1: Int, val position2: Int, val letterRequirement: Char, val password: String) {
        val isValid = (password[position1 - 1] == letterRequirement) xor (password[position2 - 1] == letterRequirement)
    }
}

fun main() {
    println(Day2.part1(Day2.part1Input))
}
