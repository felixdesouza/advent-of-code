package aoc2022

import common.Problem
import java.util.*

object Day5 : Problem() {
    private val moveRegex = "move (\\d+) from (\\d+) to (\\d+)".toRegex()
    private val stackItemRegex = "\\[([A-Z])]".toRegex()

    val input = rawInput
    val testInput = """
            [D]    
        [N] [C]    
        [Z] [M] [P]
         1   2   3 
        
        move 1 from 2 to 1
        move 3 from 1 to 3
        move 2 from 2 to 1
        move 1 from 1 to 2
    """.trimIndent()

    private fun parseInput(rawInput: String): Pair<Map<Int, Deque<String>>, List<Move>> {
        val (stackLines, moves) = rawInput.split("\n\n")
        val numStacks = (stackLines.lines().first().length + 1) / 4

        val stacks = (1..numStacks).associateWith { LinkedList<String>() }.toMutableMap()
        stackLines.lines().dropLast(1).reversed().forEachIndexed{ _, line ->
            line.chunked(4).forEachIndexed { stack, cell ->
                stackItemRegex.find(cell)?.destructured?.component1()?.apply { stacks[stack + 1]!!.push(this) }
            }
        }

        val parsedMoves = moves.lines().map { parseMove(it) }
        return stacks to parsedMoves
    }

    private fun parseMove(line: String): Move {
        val (stack, from, to) = moveRegex.matchEntire(line)?.destructured ?: throw AssertionError("does not match")
        return Move(stack.toInt(), from.toInt(), to.toInt())
    }

    fun part1(input: Pair<Map<Int, Deque<String>>, List<Move>>): String {
        val (stacks, moves) = input

        moves.forEach { move ->
            val to = stacks[move.to]!!
            val from = stacks[move.from]!!
            
            for (i in 1..move.quantity) {
                to.push(from.pop()!!)
            }
        }

        return topOfStacks(stacks)
    }

    fun part2(input: Pair<Map<Int, Deque<String>>, List<Move>>): String {
        val (stacks, moves) = input

        moves.forEach { move ->
            val to = stacks[move.to]!!
            val from = stacks[move.from]!!

            val temp = mutableListOf<String>()
            for (i in 1..move.quantity) {
                temp.add(from.pop())
            }
            temp.reversed().forEach { to.push(it) }
        }

        return topOfStacks(stacks)
    }

    private fun topOfStacks(stacks: Map<Int, Deque<String>>): String {
        return stacks.values.mapNotNull { it.peek() }.joinToString(separator = "")
    }

    data class Move (val quantity: Int, val from: Int, val to: Int)

    @JvmStatic
    fun main(args: Array<String>) {
        println(part1(parseInput(input)))
        println(part2(parseInput(input)))
    }
}

