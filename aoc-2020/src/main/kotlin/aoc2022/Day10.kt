package aoc2022

import common.Problem
import common.openFile

object Day10 : Problem() {

    val input = rawInput.let { parse(it) }
    val testInput = """
        noop
        addx 3
        addx -5
    """.trimIndent().let { parse(it) }

    val testInput2 = openFile("/aoc2022/test.day10.txt").trimIndent().let { parse(it) }
    val testInput2Result = """
        ##..##..##..##..##..##..##..##..##..##..
        ###...###...###...###...###...###...###.
        ####....####....####....####....####....
        #####.....#####.....#####.....#####.....
        ######......######......######......####
        #######.......#######.......#######.....
    """.trimIndent()

    val inputResult = """
        ###..#..#..##...##...##..###..#..#.####.
        #..#.#..#.#..#.#..#.#..#.#..#.#..#....#.
        ###..#..#.#....#..#.#....###..#..#...#..
        #..#.#..#.#....####.#....#..#.#..#..#...
        #..#.#..#.#..#.#..#.#..#.#..#.#..#.#....
        ###...##...##..#..#..##..###...##..####.
    """.trimIndent()

    private fun parse(input: String): List<Instruction> = input.lines().map {
        when {
            it.startsWith("noop") -> Noop
            it.startsWith("addx") -> Addx(it.substringAfter("addx ").toInt())
            else -> throw AssertionError("error parsing")
        }
    }

    fun part1(input: List<Instruction>): Int {
        val simulate = simulate(input, ((60..220 step 40) + 20).toSet())
        return simulate.map { (cycle, value) -> cycle * value }.sum()
    }

    fun part2(input: List<Instruction>): String {
        val simulate = simulate(input, (1..240).toSet())

        val coordinates = simulate.mapValues { (cycle, registerValue) ->
            val spritePosition = registerValue - 1..registerValue + 1
            val position = (cycle - 1) % 40
            if (position in spritePosition) '#' else '.'
        }

        return (1..240)
                .map { coordinates[it] }
                .joinToString(separator = "")
                .chunked(40)
                .joinToString("\n")
    }

    private fun simulate(input: List<Instruction>, cyclesToTrack: Set<Int>): Map<Int, Int> {
        data class State(val cycle: Int, val register: Int, val trackedCycles: Map<Int, Int>)
        return input.fold(State(1, 1, emptyMap())) { (cycle, register, trackedCycles), instruction ->
            val newCycle = cycle + instruction.cycleCount
            val newRegister = instruction.execute(register)
            val newTrackedCycles = trackedCycles +
                    (cycle until newCycle).intersect(cyclesToTrack).associateWith { register } +
                    if (newCycle in cyclesToTrack) sequenceOf(newCycle to newRegister) else sequenceOf()

            State(newCycle, newRegister, newTrackedCycles)
        }.trackedCycles
    }

    interface Instruction {
        val cycleCount: Int
        fun execute(register: Int): Int
    }

    object Noop : Instruction {
        override val cycleCount = 1
        override fun execute(register: Int) = register
        override fun toString(): String {
            return "Noop"
        }
    }

    data class Addx(val arg: Int) : Instruction {
        override val cycleCount = 2
        override fun execute(register: Int) = register + arg
    }

    @JvmStatic
    fun main(args: Array<String>) {
        println(part1(input))
        println(part2(testInput2) == testInput2Result)
        println(part2(input) == inputResult)
    }
}

