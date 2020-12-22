package aoc2020

import common.openFile
import java.util.*

object Day22 {

    val input = openFile("/aoc2020/day22.txt").let { parseInput(it) }

    val testInput = """
        Player 1:
        9
        2
        6
        3
        1

        Player 2:
        5
        8
        4
        7
        10
    """.trimIndent()
            .let { parseInput(it) }

    fun parseInput(raw: String): Pair<Player, Player> {
        val (player1, player2) = raw.split("\n\n").map { Player.parsePlayer(it) }.let { LinkedList(it) }
        return player1 to player2
    }

    data class Player(val deck: LinkedList<Int>) {
        companion object {
            fun parsePlayer(raw: String): Player {
                return Player(raw.lines().drop(1).map { it.toInt() }.let { LinkedList(it) })
            }
        }
    }

    fun Pair<Player, Player>.tick(): Pair<Player, Player> {
        val (player1, player2) = this
        if (player1.deck.isEmpty() || player2.deck.isEmpty()) {
            throw AssertionError("game is over")
        }

        val p1 = player1.deck.removeFirst()
        val p2 = player2.deck.removeFirst()

        val sorted = listOf(p1, p2).sortedDescending()

        if (p1 > p2) {
            player1.deck.addAll(sorted)
        } else {
            player2.deck.addAll(sorted)
        }

        return player1 to player2
    }

    fun Pair<Player, Player>.winner(): Player? {
        val (player1, player2) = this
        val loser = listOf(player1, player2).firstOrNull { it.deck.isEmpty() }

        return loser?.let { (listOf(player1, player2) - it).first() }
    }

    fun part1(state: Pair<Player, Player>): Long {
        fun game(state: Pair<Player, Player>): Player {
            return state.winner() ?: game(state.tick())
        }

        val winner = game(state)
        return (winner.deck.size downTo 1).zip(winner.deck).map { (score, card) -> score * card.toLong() }.sum()
    }
}

fun main() {
    println(Day22.part1(Day22.input))
}