package aoc2021

import com.google.common.collect.Queues
import common.Coordinate
import java.lang.Math.abs
import java.lang.Math.min
import java.time.Duration
import java.time.Instant
import java.util.*

object Day23 {

    val input = """
        #############
        #...........#
        ###D#C#A#B###
          #C#D#A#B#
          #########
    """.trimIndent()
            .let { Game.parse(it) }

    val testInput = """
        #############
        #...........#
        ###B#C#B#D###
          #A#D#C#A#
          #########
    """.trimIndent()
            .let { Game.parse(it) }

    class Anthropods(val anthropods: Set<Anthropod>) {
        val podDepth = anthropods.minBy { it.verticalPosition }!!.verticalPosition
        val key: String by lazy(LazyThreadSafetyMode.NONE) {
            val byCoordinate = anthropods.associateBy { Coordinate(it.horizontalPosition, it.verticalPosition) }
            val hallway = (1..11).map { Coordinate(it, 0) }.joinToString("") {
                if (it in byCoordinate) "${byCoordinate[it]!!.letter}" else "."
            }

            val pods = (-1 downTo podDepth).joinToString("") { y ->
                (3..9 step 2).map { x ->
                    Coordinate(x, y)
                }.joinToString("") { if (it in byCoordinate) "${byCoordinate[it]!!.letter}" else "." }
            }
            hallway + pods
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as Anthropods

            if (key != other.key) return false

            return true
        }

        override fun hashCode(): Int {
            return key.hashCode()
        }

        override fun toString(): String {
            val byCoordinate = anthropods.associateBy { Coordinate(it.horizontalPosition, it.verticalPosition) }
            val hallway = (1..11).map { Coordinate(it, 0) }.joinToString("") {
                if (it in byCoordinate) "${byCoordinate[it]!!.letter}" else "."
            }

            val pods = (-1 downTo podDepth).joinToString("\n") { y ->

                val sidepods = (3..9).map { x ->
                    Coordinate(x, y)
                }.joinToString("") {
                    when {
                        it in byCoordinate -> "${byCoordinate[it]!!.letter}"
                        it.x % 2 == 0 -> "|"
                        else -> "."
                    }
                }
                "||$sidepods||"
            }
            val ceiling = (1..11).joinToString("") { "-" }
            val floor = "-----------"
            return "$ceiling\n$hallway\n$pods\n$floor"
        }


    }


    data class Anthropod(val letter: Char,
                         val horizontalPosition: Int,
                         val verticalPosition: Int) {
        companion object {
            val desiredPosition = mapOf('A' to 3, 'B' to 5, 'C' to 7, 'D' to 9)
            val energy = mapOf('A' to 1, 'B' to 10, 'C' to 100, 'D' to 1000)
        }

        val desiredHorizontalPosition = desiredPosition[letter]!!
        val energyRequired = energy[letter]!!
        val inOwnSidepod = horizontalPosition == desiredHorizontalPosition
        val inHallway = verticalPosition == 0
    }

    data class Game(val anthropods: Anthropods, val energySpent: Int = 0) : Comparable<Game> {
        companion object {
            val sidepods = setOf(3, 5, 7, 9)

            val comparator = compareBy<Game> { it.minPotential + it.energySpent }


            val part2pods = setOf(
                    Anthropod('D', 3, -2),
                    Anthropod('C', 5, -2),
                    Anthropod('B', 7, -2),
                    Anthropod('A', 9, -2),
                    Anthropod('D', 3, -3),
                    Anthropod('B', 5, -3),
                    Anthropod('A', 7, -3),
                    Anthropod('C', 9, -3)
            )

            fun parse(string: String): Game {
                return string.lines().drop(2)
                        .withIndex()
                        .map { (negativeVerticalPosition, char) -> -(negativeVerticalPosition + 1) to char }
                        .take(2)
                        .flatMap { (y, string) ->
                            string.withIndex().filter { it.value in 'A'..'D' }
                                    .map { (horizontalPosition, anthropod) ->
                                        Anthropod(anthropod, horizontalPosition, y)
                                    }
                        }
                        .toSet()
                        .let { Game(Anthropods(it)) }
            }
        }

        val podDepth = abs(anthropods.podDepth)

        val podsByHorizontalPosition = anthropods.anthropods
                .groupBy { it.horizontalPosition }
                .mapValues { it.value.sortedBy { it.verticalPosition }.reversed() }

        // if in own sidepod then no need to do anything
        // it not in own sidepod, then need to get there and need to move whatever is in sidepod

        fun Anthropod.minPotential(): Int {
            return if (inOwnSidepod) {
                (3 + abs(verticalPosition)) * energyRequired
            } else {
                val x = abs(horizontalPosition - desiredHorizontalPosition)
                val y = 1 + abs(verticalPosition)
                (x + y) * energyRequired
            }
        }


        fun Anthropod.isInFinalPosition(): Boolean {
            if (!inOwnSidepod) {
                return false
            }

            return podsByHorizontalPosition[horizontalPosition]!!
                    .asSequence()
                    .filter { abs(it.verticalPosition) > abs(this.verticalPosition) }
                    .map { it.letter }
                    .all { it == this.letter }
        }

        val minPotential: Int =
                anthropods.anthropods.asSequence().filterNot { it.isInFinalPosition() }.sumBy { it.minPotential() }

        val numberCantMove: Int =
                anthropods.anthropods.asSequence().filterNot { it.isInFinalPosition() }
                        .filter { podsByHorizontalPosition[it.horizontalPosition]!!.first() != it }
                        .count()

        fun nextStates(): Set<Game> {
            val anthropodsAbleToMove = podsByHorizontalPosition
                    // anthropods in their own sidepod with no other anthropods are in their final positions
                    .mapValues { it.value.filterNot { it.isInFinalPosition() } }
                    .filterValues { it.isNotEmpty() }
                    // only highest anthropod can move
                    .mapValues { (x, pods) -> pods.first() }

            val (hallwayPodsToMove, sidepodPodsToMove) = anthropodsAbleToMove.values.partition { it.inHallway }
            val hallwayPositions = hallwayPodsToMove.map { it.horizontalPosition }.toSortedSet() as TreeSet

            val availableSidepods = sidepods.filter {
                val sidepodContents = podsByHorizontalPosition.get(it) ?: emptyList()
                sidepodContents.isEmpty() || (abs(sidepodContents.first().verticalPosition) > 1 && sidepodContents.map { it.letter }.toSet().size == 1)
            }.toSet()

            // if in a hallway can you get to your sidepod?
            val hallwayGames = hallwayPodsToMove
                    // sidepod can accept this pod
                    .filter { it.desiredHorizontalPosition in availableSidepods }
                    // no obstacle between current position and sidepod
                    .filter {
                        if (it.horizontalPosition < it.desiredHorizontalPosition) {
                            hallwayPositions.tailSet(it.horizontalPosition, false).headSet(it.desiredHorizontalPosition, false).isEmpty()
                        } else {
                            hallwayPositions.tailSet(it.desiredHorizontalPosition, false).headSet(it.horizontalPosition, false).isEmpty()
                        }
                    }
                    // create new Games from here
                    .map { anthropod ->
                        val x = abs(anthropod.horizontalPosition - anthropod.desiredHorizontalPosition)
                        val podsInSidepod = podsByHorizontalPosition[anthropod.desiredHorizontalPosition] ?: emptyList()
                        val y = when (val size = podsInSidepod.size) {
                            in 0 until podDepth -> podDepth - size
                            else -> throw AssertionError("sidepod not empty!!")
                        }
                        val spentEnergy = (x + y) * anthropod.energyRequired
                        val newAnthropod = anthropod.copy(horizontalPosition = anthropod.desiredHorizontalPosition, verticalPosition = -y)
                        val newAnthropods = anthropods.anthropods.toMutableSet()
                        newAnthropods.remove(anthropod)
                        newAnthropods.add(newAnthropod)

                        Game(anthropods = Anthropods(newAnthropods), energySpent = energySpent + spentEnergy)
                    }


            val availableHallwayPositions = (1..11)
                    // cant stop in front of a sidepod door
                    .filterNot { it in sidepods }
                    // can't occupy another space that has an anthropod on it
                    .filterNot { it in hallwayPositions }.toSortedSet() as TreeSet


            // if in sidepod, go to every point available in hallway
            val sidepodGames = sidepodPodsToMove.associateWith { anthropod ->
                val max = hallwayPositions.ceiling(anthropod.horizontalPosition) ?: 12
                val min = hallwayPositions.floor(anthropod.horizontalPosition) ?: 0

                val availableHallwayPositionsForPod = availableHallwayPositions.headSet(max, false).tailSet(min, false)
                availableHallwayPositionsForPod
            }
                    .flatMap { (anthropod, hallwayPositions) ->
                        hallwayPositions.map { hallwayPosition ->
                            val deltaY = abs(anthropod.verticalPosition)
                            val deltaX = abs(hallwayPosition - anthropod.horizontalPosition)
                            val spentEnergy = (deltaY + deltaX) * anthropod.energyRequired
                            val newAnthropod = anthropod.copy(horizontalPosition = hallwayPosition, verticalPosition = 0)
                            val newAnthropods = anthropods.anthropods.toMutableSet()
                            newAnthropods.remove(anthropod)
                            newAnthropods.add(newAnthropod)

                            Game(anthropods = Anthropods(newAnthropods), energySpent = energySpent + spentEnergy)
                        }
                    }

            return (sidepodGames + hallwayGames).toSet()
        }

        val numInFinalPositions = anthropods.anthropods.count { it.isInFinalPosition() }

        fun isOver() = numInFinalPositions == anthropods.anthropods.size

        override fun compareTo(other: Game) = comparator.compare(this, other)

        override fun toString(): String {
            return """
                Min potential: ${minPotential + energySpent}
                Number cant move: $numberCantMove
                Number in final positions: $numInFinalPositions
                
            """.trimIndent() + anthropods.toString()
        }
    }

    fun parts(input: Game) {
        println("---")
        println("part 1: ${run(input)}")


        val part2Pods = (input.anthropods.anthropods.map { if (it.verticalPosition == -2) it.copy(verticalPosition = -4) else it } + Game.part2pods)
                .let { Anthropods(it.toSet()) }
        val part2Input = input.copy(anthropods = part2Pods)
        println("---")
        println("part 2: ${run(part2Input)}")

    }

    fun run(input: Game): Int {
        println(input)

        val candidates = Queues.newPriorityQueue<Game>(listOf(input))
        var minScore = Int.MAX_VALUE
        var minCandidate: Game? = null
        // m is best seen so far for this set of pods
        val m = hashMapOf(input.anthropods to input)

        val processingStates = hashSetOf(input.anthropods)
        val cameFrom = mutableMapOf<Anthropods, Anthropods>()

        var count = 0
        var skipped = 0
        var last = Instant.now()
        while (candidates.isNotEmpty()) {
            if (count % 10000 == 0) {
                val next = Instant.now()
                val diff = Duration.between(last, next).toMillis()
                last = next
                println("$diff total seen ${m.size}")
            }
            count += 1
            val candidate = candidates.poll()!!
            processingStates.remove(candidate.anthropods)

            if (candidate.energySpent >= minScore) {
                continue
            }

            if (candidate.minPotential + candidate.energySpent > (minCandidate?.let { it.minPotential + it.energySpent }
                            ?: Int.MAX_VALUE)) {
                println("exhausted")
                break
            }

            if (candidate.isOver()) {
                minScore = min(minScore, candidate.energySpent)
                minCandidate = candidate
                println("new min score $minScore")
                println("$minCandidate")
                continue
            }

            val newCandidates = candidate.nextStates()

            for (newCandidate in newCandidates) {
                val bla = m[newCandidate.anthropods]
                if (newCandidate.energySpent < (bla?.energySpent ?: Int.MAX_VALUE)) {
                    cameFrom[newCandidate.anthropods] = candidate.anthropods
                    candidates.add(newCandidate)
                    m[newCandidate.anthropods] = newCandidate
                } else {
                    skipped++
                }
            }
        }

        println("found candidate")
        generateSequence(minCandidate!!.anthropods) { a -> cameFrom[a] }.toList().reversed().forEach {
            println(m[it]!!.energySpent)
            println(it)
        }
        return minScore
    }

    @JvmStatic
    fun main(args: Array<String>) {
        parts(testInput)
        parts(input)
    }

}