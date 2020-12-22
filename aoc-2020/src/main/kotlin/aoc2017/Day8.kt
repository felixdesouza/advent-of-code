package aoc2017

import com.github.h0tk3y.betterParse.combinators.*
import com.github.h0tk3y.betterParse.grammar.Grammar
import com.github.h0tk3y.betterParse.grammar.parseToEnd
import com.github.h0tk3y.betterParse.lexer.literalToken
import com.github.h0tk3y.betterParse.lexer.regexToken
import com.github.h0tk3y.betterParse.parser.Parser
import common.readLines
import kotlin.math.max

object Day8 {

    enum class Modifier(val fn: (a: Int, b: Int) -> Int) {
        INC({ a, b -> a + b }),
        DEC({ a, b -> a - b });

        operator fun invoke(a: Int, b: Int): Int = fn(a, b)
    }

    enum class Operator(val fn: (a: Int, b: Int) -> Boolean) {
        GT({ a, b -> a > b }),
        GTE({ a, b -> a >= b }),
        LT({ a, b -> a < b }),
        LTE({ a, b -> a <= b }),
        NEQ({ a, b -> a != b }),
        EQ({ a, b -> a == b });

        operator fun invoke(a: Int, b: Int): Boolean = fn(a, b)
    }

    data class Condition(val source: String, val operator: Operator, val value: Int) {
        fun apply(registers: Map<String, Int>): Boolean = operator(registers.getOrDefault(source, 0), value)
    }

    data class Change(val register: String, val modifier: Modifier, val change: Int) {
        operator fun invoke(registers: Map<String, Int>) = modifier(registers.getOrDefault(register, 0), change)
    }

    data class Instruction(val change: Change, val condition: Condition)

    class InstructionParser : Grammar<Instruction>() {
        val incToken by literalToken("inc")
        val decToken by literalToken("dec")
        val ifToken by literalToken("if")
        val wordToken by regexToken("[a-z]+")
        val space by regexToken("\\s+")
        val negationToken by literalToken("-")
        val numberToken by regexToken("[0-9]+")

        val gteParser by literalToken(">=")
        val gtParser by literalToken(">")
        val lteParser by literalToken("<=")
        val ltParser by literalToken("<")
        val eqParser by literalToken("==")
        val neqParser by literalToken("!=")

        val wordParser by wordToken use { text }
        val negationParser by optional(negationToken) map { it?.let { -1 } ?: 1 }
        val positiveNumberParser by numberToken map { it.text.toInt() }
        val operatorParser by (gteParser asJust Operator.GTE) or (gtParser asJust Operator.GT) or (lteParser asJust Operator.LTE) or (ltParser asJust Operator.LT) or (eqParser asJust Operator.EQ) or (neqParser asJust Operator.NEQ)

        val numberParser by negationParser * positiveNumberParser map { (negation, number) -> negation * number }
        val modifierParser by (incToken asJust Modifier.INC) or (decToken asJust Modifier.DEC)

        val changeParser by wordParser * -space * modifierParser * -space * numberParser map { (register, modifier, number) -> Change(register, modifier, number) }
        val conditionParser by wordParser * -space * operatorParser * -space * numberParser map { (register, operator, number) -> Condition(register, operator, number) }

        override val rootParser: Parser<Instruction> by changeParser * -space * -ifToken * -space * conditionParser map { (change, condition) -> Instruction(change, condition) }
    }

    val parser = InstructionParser()

    val input = readLines("/aoc2017/day8.txt").map { parser.parseToEnd(it) }

    fun part1(instructions: List<Instruction>): Int = run(instructions).first.values.max()!!

    fun part2(instructions: List<Instruction>): Int = run(instructions).second

    private fun run(instructions: List<Instruction>): Pair<Map<String, Int>, Int> {
        return instructions.fold(emptyMap<String, Int>() to Integer.MIN_VALUE) { state, nextInstruction ->
            val (registers, maxSeen) = state
            val (change, condition) = nextInstruction
            if (condition.apply(registers)) {
                val newValue = change(registers)
                (registers + (change.register to newValue)) to max(maxSeen, newValue)
            } else {
                registers to maxSeen
            }
        }
    }
}

fun main() {
    val testInput = """
        b inc 5 if a > 1
        a inc 1 if b < 5
        c dec -10 if a >= 1
        c inc -20 if c == 10
    """.trimIndent().lines().map { Day8.parser.parseToEnd(it) }

    Day8.part2(Day8.input).let { println(it) }
}