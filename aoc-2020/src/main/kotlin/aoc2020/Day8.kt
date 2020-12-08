package aoc2020

import common.readLines
import java.lang.AssertionError

object Day8 {

    enum class OpCode {
        ACC, JUMP, NOOP
    }

    data class Instruction(val opCode: OpCode, val operand: Int)

    val input = readLines("/aoc2020/day8.txt").map { parseLine(it) }

    fun parseLine(line: String): Instruction {
        val (opcode, operand) = line.split(" ")
        val parsed = when (opcode) {
            "acc" -> OpCode.ACC
            "jmp" -> OpCode.JUMP
            "nop" -> OpCode.NOOP
            else -> throw AssertionError("invalid: $opcode")
        }

        return Instruction(parsed, operand.toInt())
    }

    fun part1(instructions: List<Instruction>): Int {
        val visited = mutableSetOf<Int>()
        var instructionPointer = 0
        var accumulator = 0

        while (!(instructionPointer in visited)) {
            visited.add(instructionPointer)
            val instruction = instructions.get(instructionPointer)

            when (instruction.opCode) {
                OpCode.ACC -> {
                    accumulator += instruction.operand
                    instructionPointer += 1
                }
                OpCode.JUMP -> {
                    instructionPointer += instruction.operand
                }
                OpCode.NOOP -> {
                    instructionPointer += 1
                }
            }
        }

        return accumulator
    }
}