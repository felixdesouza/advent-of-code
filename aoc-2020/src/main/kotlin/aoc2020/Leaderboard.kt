package aoc2020

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.github.kittinunf.fuel.core.Headers
import com.github.kittinunf.fuel.httpGet
import java.io.File
import java.nio.file.Files
import java.nio.file.attribute.BasicFileAttributes
import java.time.Duration
import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZoneOffset

object Leaderboard {


}

data class Level(@JsonProperty("get_star_ts") val timestamp: String) {
    val asOffset = Instant.ofEpochSecond(timestamp.toLong()).atOffset(
        ZoneOffset.UTC
    )
}

data class Day(@JsonProperty("1") val dayOne: Level, @JsonProperty("2") val dayTwo: Level?)
data class Member(
    @JsonProperty("name") val name: String?,
    @JsonProperty("id") val id: String,
    @JsonProperty("completion_day_level") val completion: Map<String, Day>
)

data class LeaderboardJson(@JsonProperty("members") val members: Map<String, Member>)

private fun loadLeaderboardJson(id: Int, staleDuration: Duration): LeaderboardJson {
    val leaderboardFile = File("build/leaderboards/$id.json")
    val isStale = !leaderboardFile.exists() ||
            Files.readAttributes(leaderboardFile.toPath(), BasicFileAttributes::class.java).lastModifiedTime()
                .toInstant().isBefore(Instant.now().minus(staleDuration))

    if (isStale) {
        println("loading from api")
        println(System.getenv("AOC_SESSION"))
        val response = "https://adventofcode.com/2021/leaderboard/private/view/$id.json".httpGet()
            .appendHeader(Headers.COOKIE to "session=${System.getenv("AOC_SESSION")}")
            .responseString()
            .third.get()

        leaderboardFile.createNewFile()
        leaderboardFile.writeText(response)
    } else {
        println("loading from cached file")
    }

    return ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        .readValue(leaderboardFile.readText())
}

fun main() {

    val id = 77023
    val duration = "PT1M"
    val year = 2020
    val top = 15
    fun startForDay(day: Int) = OffsetDateTime.of(year, 12, day, 5, 0, 0, 0, ZoneOffset.UTC)
    File("build/leaderboards").mkdirs()

    val leaderboard = loadLeaderboardJson(id, Duration.parse(duration))

    data class LeaderboardEntry(val name: String, val day: Int, val part: Int, val time: Duration)

    val tuples = leaderboard.members.values.flatMap { member ->
        member.completion.flatMap { (day, parts) ->
            val (part1, part2) = parts
            val part1Entry = LeaderboardEntry(
                member.name ?: member.id,
                day.toInt(),
                1,
                Duration.between(startForDay(day.toInt()), part1.asOffset)
            )

            val part2Entry = part2?.let {
                LeaderboardEntry(
                    member.name ?: member.id,
                    day.toInt(),
                    2,
                    Duration.between(startForDay(day.toInt()), it.asOffset)
                )
            }

            listOfNotNull(part1Entry, part2Entry)
        }
    }

    tuples.groupBy { it.day }.entries.sortedBy { it.key }.forEach { (day, tuplesForDay) ->
        tuplesForDay.groupBy { it.part }
            .mapValues { (_, tuplesForPart) ->
                val sorted = tuplesForPart.sortedBy { it.time }.withIndex().map { it.index + 1 to it.value }
                val top30 = sorted.take(top)
                val me =
                    sorted.find { (_, entry) -> entry.name == "Felix de Souza" }
                top30.plus(me).distinct().filterNotNull()
            }
            .forEach { part, tuplesForPart ->
                println("Day $day Part $part top $top")
                println("--------------------------")
                tuplesForPart.forEach { (place, value) ->
                    val (name, _, _, time) = value
                    println("${place}: $name - $time")
                }
                println()
            }
    }

}