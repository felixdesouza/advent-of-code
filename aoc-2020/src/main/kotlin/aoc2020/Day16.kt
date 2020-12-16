package aoc2020

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
}

fun main() {
    println(Day16.part1(Day16.input))
}