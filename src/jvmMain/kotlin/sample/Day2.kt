package sample

import openFile
import sample.Day2.part2

data class NounVerb(val noun: Int, val verb: Int)
object Day2 {

    val input = parseInput(openFile("sample/day2.txt")).toMutableList()

    fun parseInput(rawInput: String) = rawInput.split(",").map(String::toInt)

    fun part1(program: List<Int>): Int {
        return runProgram(program, NounVerb(12, 2))
    }

    fun part2(program: List<Int>) {
        val noun = (1969 - 145) / 48
        val verb = 92
        val parameters = NounVerb(noun, verb)
        println(runProgram(program, parameters))
        println(parameters)
    }

    private fun runProgram(program: List<Int>, parameters: NounVerb): Int {
        val mutable = program.toMutableList()
        mutable[1] = parameters.noun
        mutable[2] = parameters.verb

        var index = 0
        loop@ while (true) {
            val opcode = mutable[index]

            when (opcode) {
                1 -> mutate(mutable, index) { a, b -> a + b }
                2 -> mutate(mutable, index) { a, b -> a * b }
                99 -> {
//                    println(mutable)
                    break@loop
                }
                else -> throw UnsupportedOperationException("unexpected opcode: $opcode")
            }
            index += 4
//            println(mutable)
        }

        return mutable[0]
    }

    private fun mutate(mutable: MutableList<Int>, index: Int, function: (arg1: Int, arg2: Int) -> Int) {
        val (arg1, arg2, resultRegister) = mutable.slice(index + 1 .. index + 3)
        mutable[resultRegister] = function.invoke(mutable[arg1], mutable[arg2])

    }
}

fun main() {
//    println("${part1(Day2.input)}")
    part2(Day2.input)
}

