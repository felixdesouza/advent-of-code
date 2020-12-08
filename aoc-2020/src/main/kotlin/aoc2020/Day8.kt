package aoc2020

import common.readLines

object Day8 {

    enum class OpCode {
        ACC, JUMP, NOOP
    }

    data class Instruction(val opCode: OpCode, val operand: Int)

    data class ExecutionResult(val accumulator: Int, val terminatedSuccessfully: Boolean)

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
        return runInstructions(instructions).accumulator
    }

    fun part2(instructions: List<Instruction>): Int {
        return instructions.withIndex()
            .filter { (_, instruction) -> instruction.opCode != OpCode.ACC }
            .map { it.index }
            .asSequence()
            .map { indexToSwitch ->
                val newInstructions = instructions.toMutableList()
                val (opCode, operand) = instructions[indexToSwitch]
                val newOpCode = when (opCode) {
                    OpCode.ACC -> throw AssertionError("should not be here")
                    OpCode.JUMP -> OpCode.NOOP
                    OpCode.NOOP -> OpCode.JUMP
                }

                val newInstruction = Instruction(newOpCode, operand)
                newInstructions[indexToSwitch] = newInstruction
                runInstructions(newInstructions)
            }
            .first { it.terminatedSuccessfully }.accumulator
    }

    private fun runInstructions(instructions: List<Instruction>): ExecutionResult {
        val visited = mutableSetOf<Int>()
        var instructionPointer = 0
        var accumulator = 0

        while (!(instructionPointer in visited) && instructionPointer != instructions.size) {
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

        val terminatedSuccessfully = !(instructionPointer in visited)
        return ExecutionResult(accumulator, terminatedSuccessfully)
    }
}