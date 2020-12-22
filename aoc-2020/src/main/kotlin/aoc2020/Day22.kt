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

    val testInput2 = """
        Player 1:
        43
        19

        Player 2:
        2
        29
        14
    """.trimIndent().let { parseInput(it) }

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

        fun clone(n: Int = deck.size): Player {
            if (deck.size < n) throw AssertionError("something is wrong")
            return Player(LinkedList(deck.take(n)))
        }
        fun shouldSubgame(n: Int): Boolean = deck.size >= n

        fun score(): Long = (deck.size downTo 1).zip(deck).map { (score, card) -> score * card.toLong() }.sum()
    }

    fun game(state: State): Player {
        return state.winner() ?: game(state.tick())
    }

    data class State(val player1: Player, val player2: Player, val previousState: Set<Pair<Player, Player>>, val recursive: Boolean) {
        fun winner(): Player? {
            if (recursive && (player1 to player2) in previousState) {
                return player1
            }
            val loser = listOf(player1, player2).firstOrNull { it.deck.isEmpty() }
            return loser?.let { (listOf(player1, player2) - it).first() }
        }

        fun tick(): State {
            if (player1.deck.isEmpty() || player2.deck.isEmpty()) {
                throw AssertionError("game is over")
            }

            val savedPlayer1 = player1.clone()
            val savedPlayer2 = player2.clone()

//            println("begin tick: player 1: $player1 player 2: $player2")

            val p1 = player1.deck.removeFirst()
            val p2 = player2.deck.removeFirst()

            val winner = if (recursive && player1.shouldSubgame(p1) && player2.shouldSubgame(p2)) {
                val newPlayer1 = player1.clone(p1)
                val newPlayer2 = player2.clone(p2)

                val winner = game(State(newPlayer1, newPlayer2, emptySet(), recursive))
                if (winner == newPlayer1) player1 else player2
            } else {
                if (p1 > p2) player1 else player2
            }

            when (winner) {
                player1 -> {
                    player1.deck.add(p1)
                    player1.deck.add(p2)
                }
                player2 -> {
                    player2.deck.add(p2)
                    player2.deck.add(p1)
                }
                else -> {
                    throw AssertionError("unexpected winner")
                }
            }

            return copy(previousState = previousState.plus(savedPlayer1 to savedPlayer2))
        }
    }

    fun part1(state: Pair<Player, Player>): Long {
        return game(State(state.first, state.second, emptySet(), false)).score()
    }

    fun part2(state: Pair<Player, Player>): Long {
        return game(State(state.first, state.second, emptySet(), true)).score()
    }
}

fun main() {
//    println(Day22.part1(Day22.input))
    println(Day22.part2(Day22.input))
}