package aoc2021

import java.lang.Long.max

object Day21 {

    val input = Game(
            Player(0, 1),
            Player(0, 6),
            Die.new,
            player1Turn = true)

    val testInput = Game(
            Player(0, 3),
            Player(0, 7),
            Die.new,
            player1Turn = true)

    data class Die(val next: Int, val count: Int) {
        companion object {
            val new = Die(0, 0)
        }

        fun roll() = (next..next + 2).map { (it % 100) + 1 }.sum() to Die((next + 3) % 100, count + 3)
    }

    data class Player(val score: Int, val position: Int) {
        fun move(n: Int): Player {
            val newPosition = (position + n) % 10
            val newScore = score + (newPosition + 1)
            return Player(newScore, newPosition)
        }
    }

    data class Game(val player1: Player, val player2: Player, val die: Die, val player1Turn: Boolean) {
        fun iterate(): Game {
            val (r, d) = die.roll()
            val player = if (player1Turn) player1 else player2
            val updatedPlayer = player.move(r)
            return Game(
                    player1 = if (player1Turn) updatedPlayer else player1,
                    player2 = if (!player1Turn) updatedPlayer else player2,
                    die = d,
                    player1Turn = !player1Turn
            )
        }

        fun isOver() = player1.score >= 1000 || player2.score >= 1000
    }

    fun parts(input: Game) {
        val gameOver = generateSequence(input) { it.iterate() }.dropWhile { !it.isOver() }.first()
        val part1 = minOf(gameOver.player1.score, gameOver.player2.score) * gameOver.die.count
        println("part 1: $part1")
        println("part 2: ${exponentialSolve(input)}")
    }

    fun exponentialSolve(game: Game): Long {
        val universePerScore = mapOf(
                3 to 1,
                4 to 3,
                5 to 6,
                6 to 7,
                7 to 6,
                8 to 3,
                9 to 1)

        // 1 universe with the initial game state
        val seed = mapOf((game.player1 to game.player2) to 1L)

        val seq = generateSequence(seed to true) { (scoresAndUniverses, player1Turn) ->
            val (complete, toPlay) = scoresAndUniverses.entries.partition { it.key.first.score >= 21 || it.key.second.score >= 21 }
            val completePairs = complete.map { (state, universes) -> state to universes }
            val newScoresAndUniversesUnmerged = toPlay.flatMap { (state, universes) ->
                val (player1, player2) = state

                val player = if (player1Turn) player1 else player2
                (3..9).map { diceRoll ->
                    player.move(diceRoll) to universes * universePerScore[diceRoll]!!
                }.map { (newPlayer, newUniverses) ->
                    val newState = if (player1Turn) state.copy(first = newPlayer) else state.copy(second = newPlayer)
                    newState to newUniverses
                }
            }

            val newScoresAndUniverses = (completePairs + newScoresAndUniversesUnmerged).groupingBy { it.first }
                    .fold(0L) { accumulator, element -> accumulator + element.second }

            newScoresAndUniverses to !player1Turn
        }.map { it.first }

        val endState = seq.first { it.keys.all { (player1, player2) -> player1.score >= 21 || player2.score >= 21 } }
        val victories = endState.entries.fold(0L to 0L) { (player1Victories, player2Victories), (players, universes) ->
            val (player1, player2) = players
            when {
                player1.score >= 21 -> player1Victories + universes to player2Victories
                player2.score >= 21 -> player1Victories to player2Victories + universes
                else -> throw AssertionError("should not happen")
            }
        }

        return max(victories.first, victories.second)
    }

    @JvmStatic
    fun main(args: Array<String>) {
        parts(testInput)
        parts(input)
    }

}