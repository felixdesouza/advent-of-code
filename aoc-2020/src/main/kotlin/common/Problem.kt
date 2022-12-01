package common

import com.github.kittinunf.fuel.core.Headers
import com.github.kittinunf.fuel.httpGet
import java.io.File

abstract class Problem {
    companion object {
        val aocRegex = "^aoc(\\d+)$".toRegex()
        val dayRegex = "Day(\\d+)$".toRegex()
    }

    val rawInput = fetchInput()

    private fun fetchInput(): String {
        val (year) = aocRegex.find(this.javaClass.packageName)?.destructured
                ?: throw RuntimeException("package not in aoc<year> format")

        val (day) = dayRegex.find(this.javaClass.simpleName)?.destructured
                ?: throw java.lang.RuntimeException("class name not in Day<day> format")

        val yearDirectory = File("build/input/${year.toInt()}/")
        yearDirectory.mkdirs()

        val inputFile = yearDirectory.resolve("day${day.toInt()}.txt")
        if (!inputFile.exists()) {
            println("Downloading input for day ${day.toInt()}")
            val response = "https://adventofcode.com/${year.toInt()}/day/${day.toInt()}/input".httpGet()
                    .appendHeader(Headers.COOKIE to "session=${System.getenv("AOC_SESSION")}")
                    .responseString()
                    .third.get()
            inputFile.writeText(response)
        }

        return inputFile.readText().trimEnd()
    }
}