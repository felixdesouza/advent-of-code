package aoc2020

import com.google.common.collect.Queues
import common.openFile

object Day16 {

    val testInput = """
        class: 1-3 or 5-7
        row: 6-11 or 33-44
        seat: 13-40 or 45-50

        your ticket:
        7,1,14

        nearby tickets:
        7,3,47
        40,4,50
        55,2,20
        38,6,12
    """.trimIndent().let { parseInput(it) }

    val input = openFile("/aoc2020/day16.txt").let { parseInput(it) }

    data class Def(val field: String, val ranges: List<IntRange>) {
        fun contains(value: Int): Boolean {
            return ranges.any { value in it }
        }
    }

    data class Ticket(val defs: List<Def>, val myTicket: List<Int>, val nearbyTickets: List<List<Int>>)

    fun parseInput(raw: String): Ticket {
        val (defString, myTicket, nearbyTickets) = raw.split("\n\n")

        val defs = defString.lines()
            .map {
                val (field, rangeString) = it.split(": ")
                val ranges = rangeString.split(" or ").map {
                    val (start, end) = it.split("-").map { it.toInt() }
                    start..end
                }

                Def(field, ranges)
            }

        val myParsedTicket = myTicket.lines().drop(1).first()!!.split(",").map { it.toInt() }
        val nearbyParsedTickets = nearbyTickets.lines().drop(1).map { it.split(",").map { it.toInt() } }

        return Ticket(defs, myParsedTicket, nearbyParsedTickets)
    }

    fun part1(ticket: Ticket): Long {
        return ticket.nearbyTickets.flatten().filter { value -> ticket.defs.none { it.contains(value) } }.map { it.toLong() }.sum()
    }

    fun part2(ticket: Ticket): Long {
        val newNearbyTickets = ticket.nearbyTickets.filter { ticketValues -> ticketValues.all { value -> ticket.defs.any { it.contains(value) } } }

        val newTicket = ticket.copy(nearbyTickets = newNearbyTickets)

        val numFields = newTicket.defs.size

        val allFields = (0 until numFields).toSet()

        val candidatesByDef = newTicket.defs.associateWith { def ->
            val invalidLocations = newNearbyTickets
                .map { nearbyTicket ->
                    nearbyTicket.withIndex().filterNot { (_, value) -> def.contains(value) }.map { it.index }
                }
                .flatten().toSet()

            allFields.minus(invalidLocations)
        }.onEach { println(it) }

        val defsToVisit = Queues.newArrayDeque<Def>(newTicket.defs)
        val assignedFields = mutableMapOf<Int, Def>()

        while (!defsToVisit.isEmpty()) {
            val def = defsToVisit.pollFirst()

            val remainder = candidatesByDef[def]!!.minus(assignedFields.keys)
            if (remainder.size == 1) {
                assignedFields[remainder.first()] = def
            } else {
                defsToVisit.addLast(def)
            }
        }

        val fieldsByDef = assignedFields.map { (k, v) -> v to k }.toMap()

        return ticket.defs.filter { it.field.startsWith("departure") }
            .map { fieldsByDef[it]!! }
            .map { ticket.myTicket[it] }
            .map { it.toLong() }
            .reduce { acc, i -> acc * i }
    }
}

fun main() {
    println(Day16.part1(Day16.input))

    println(Day16.part2(Day16.input))
}