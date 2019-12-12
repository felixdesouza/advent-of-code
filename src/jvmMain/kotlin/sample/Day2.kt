package sample

import openFile
import sample.Day2.part1

object Day2 {

    val input = parseInput(openFile("sample/day2.txt")).toMutableList()

    fun parseInput(rawInput: String) = rawInput.split(",").map(String::toInt)

    fun part1(program: List<Int>): Int {
        val mutable = program.toMutableList()
        var index = 0
        loop@ while (true) {
            val opcode = mutable[index]

            when (opcode) {
                1 -> mutate(mutable, index) { a, b -> a + b }
                2 -> mutate(mutable, index) { a, b -> a * b }
                99 -> {
                    println(mutable)
                    break@loop
                }
                else -> throw UnsupportedOperationException("unexpected opcode: $opcode")
            }
            index += 4
            println(mutable)
        }

        return mutable[0]
    }

    private fun mutate(mutable: MutableList<Int>, index: Int, function: (arg1: Int, arg2: Int) -> Int) {
        val (arg1, arg2, resultRegister) = mutable.slice(index + 1 .. index + 3)
        mutable[resultRegister] = function.invoke(mutable[arg1], mutable[arg2])

    }
}

fun main() {
    val mutableInput = Day2.input.toMutableList()
    mutableInput[1] = 12
    mutableInput[2] = 2
    println("${part1(mutableInput)}")
}

