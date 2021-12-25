package aoc2021

import common.openFile
import java.lang.Integer.max

object Day24 {

    val input = openFile("/aoc2021/day24.txt").let { Program.parseLines(it) }

    val testInput = """
        inp x
        mul x -1
    """.trimIndent()
            .let { Program.parseLines(it) }

    data class Program(val instructions: List<String>,
                       val input: List<Long> = emptyList(),
                       val needsInput: Boolean = false,
                       val registers: Map<String, Long> = listOf("w", "x", "y", "z").associateWith { 0L },
                       val ip: Int = 0) {
        companion object {
            fun parseLines(string: String): Program {
                return string.lines().let { Program(it) }
            }
        }

        fun isOver() = ip == instructions.size

        fun step(): Program {
            if (isOver()) {
                throw AssertionError("over")
            }

            val instruction = instructions[ip]
            val bits = instruction.split(" ")
            val opcode = bits.first()

            fun binary(op: (Long, Long) -> Long): Program {
                val (resultRegister, b) = bits.drop(1)
                val first = registers[resultRegister]!!
                val second = if (b.contains("[wxyz]".toRegex())) registers[b]!! else java.lang.Long.parseLong(b)
                val result = op(first, second)
                val newRegisters = registers + (resultRegister to result)
                println("$ip registers before $instruction | $resultRegister = $first $opcode $second = $result | $registers")
                return copy(ip = ip + 1, registers = newRegisters)
            }

            return when (opcode) {
                "inp" -> {
                    if (input.isEmpty()) {
                        if (needsInput) throw AssertionError("needs input")
                        return copy(needsInput = true)
                    }
                    val value = input.first()
                    println()
                    println("$ip registers before $opcode input of $value: $registers")
                    val rest = input.drop(1)
                    val register = bits.drop(1).first()
                    val newRegisters = registers + (register to value)
                    copy(ip = ip + 1, input = rest, registers = newRegisters)
                }
                "add" -> binary { a, b -> a + b }
                "mul" -> binary { a, b -> a * b }
                "div" -> binary { a, b ->
                    if (b == 0L) throw AssertionError("div / 0 should not happen")
                    a / b
                }
                "mod" -> binary { a, b ->
                    if (a < 0 || b <= 0) throw AssertionError("a < 0 or b <= 0 disallowed $registers ")
                    a % b
                }
                "eql" -> binary { a, b -> if (a == b) 1 else 0 }
                else -> throw AssertionError("unrecognised")
            }
        }

        fun run(input: List<Long>): Program {
            val bla = generateSequence(copy(input = input)) { it.step() }.first { it.isOver() || it.needsInput }
            return bla
        }
    }

    fun checkModelNumber(modelNumber: Long): Program {
        val modelNumberAsInput = modelNumber.digits()
        return input.run(modelNumberAsInput)
    }

    fun Long.digits() = this.toString().map { it - '0' }.map { it.toLong() }

    fun parts() {
        findModelNumber(0, params.reversed(), false).also { println("part 1: $it") }
        findModelNumber(0, params.reversed(), true).also { println("part 2: $it") }
    }

    data class Param(val i: Int, val j: Int, val k: Int)

    fun findModelNumber(zTarget: Long, params: List<Param>, min: Boolean): Long? {
        if (params.isEmpty()) {
            // do we need to check if zTarget is 0?
            println("END REACHED")
            return 0
        }

        val param = params.first()
        val (i, j, k) = param

        val rest = params.drop(1)

        val newTargets = if (i < 0) {
            if (j != 26) throw AssertionError("not expected")
            val zTargetsToCheckNonMatchingInput = (1..9L).map { it to zTarget - (k + it) }
                    .filter { it.second % 26 == 0L }
                    .flatMap { (input, zTiedToMultipleTo26) ->
                        (zTiedToMultipleTo26 until zTiedToMultipleTo26 + 26)
                                .filter { newZTarget -> input != (newZTarget % 26) + i }
                                .map { it to input }
                    }
                    .groupingBy { it.first }
                    .fold(0L) { accumulator, element -> kotlin.math.max(accumulator, element.second) }

            val zFloor = zTarget * 26
            val zCeiling = (zTarget + 1) * 26
            val zTargetsToCheckMatchingInput = (zFloor until zCeiling)
                    .associateWith { newZTarget ->
                        (1..9L).filter { subInput -> subInput == newZTarget % 26 + i }.max()
                    }
                    .filterValues { it != null }
                    .mapValues { it.value!! }

            val newTargets = (zTargetsToCheckNonMatchingInput.asSequence() + zTargetsToCheckMatchingInput.asSequence())
                    .groupingBy { it.key }
                    .fold(-1L) { acc, (_, newInput) -> kotlin.math.max(acc, newInput) }

            newTargets.entries.groupingBy { it.value }.fold(emptyList<Long>()) { zTargets, (newZTarget, _) -> zTargets + newZTarget }
                    .mapKeys { it.key.toInt() }
        } else {
            val newZTargetMatching = (zTarget to ((zTarget % 26) + i).toInt()).takeIf { it.second in 1..9 }

            val newZTargetNonMatching = (1..9).map { newInput -> zTarget - newInput - k to newInput }
                    .filter { (newZTarget, _) -> newZTarget % 26 == 0L }
                    .map { (newZTarget, newInput) -> newZTarget / 26 to newInput }
                    .filter { (newZTarget, newInput) -> newZTarget + i != newInput.toLong() }
                    .toMap()

            val combined = if (newZTargetMatching == null) {
                newZTargetNonMatching
            } else {
                val (matchingZTarget, matchingZInput) = newZTargetMatching
                newZTargetNonMatching + (matchingZTarget to max(matchingZInput, newZTargetNonMatching[matchingZTarget]
                        ?: 0))
            }

            combined.entries.map { (newZTarget, newZInput) -> newZInput to newZTarget }
                    .toMap()
                    .mapValues { listOf(it.value) }
        }

        return newTargets.toSortedMap(if (min) naturalOrder() else reverseOrder())
                .asSequence()
                .mapNotNull { (newInput, newZTargets) ->
                    newZTargets.asSequence()
                            .mapNotNull { newZTarget ->
                                findModelNumber(newZTarget, rest, min)
                            }
                            .map { it * 10 + newInput }
                            .max()
                }
                .firstOrNull()
    }

    fun Long.run(input: Int, params: Param): Long {
        var z = this
        val x = (z % 26) + params.i

        z /= params.j
        return (if (x.toInt() != input) z * 26 + (input + params.k) else z)
    }

    val params = listOf(
            Param(10, 1, 0),
            Param(12, 1, 6),
            Param(13, 1, 4),
            Param(13, 1, 2),
            Param(14, 1, 9),
            Param(-2, 26, 1),
            Param(11, 1, 10),
            Param(-15, 26, 6),
            Param(-10, 26, 4),
            Param(10, 1, 6),
            Param(-10, 26, 3),
            Param(-4, 26, 9),
            Param(-1, 26, 15),
            Param(-1, 26, 5))

    @JvmStatic
    fun main(args: Array<String>) {
        parts()
    }

}