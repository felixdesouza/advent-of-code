package aoc2020

import common.openFile

object Day4 {

    val input = openFile("/aoc2020/day4.txt")

    private val requiredKeys = setOf("byr", "iyr", "eyr", "hgt", "ecl", "pid", "hcl")

    fun parseInput(rawString: String): List<Passport> {
        return rawString.split("\n\n").mapNotNull { parseValidPassports(it) }
    }

    private fun parseValidPassports(passportString: String): Passport? {
        return passportString.split(Regex("\n|\\s")).map { it.split(":") }
            .map { (key, value) -> key to value }
            .toMap()
            .takeIf { it.keys.containsAll(requiredKeys) }
            ?.let {
                Passport(
                    birthYear = it["byr"]!!.toInt(),
                    issueYear = it["iyr"]!!.toInt(),
                    expirationYear = it["eyr"]!!.toInt(),
                    height = it["hgt"]!!,
                    hairColour = it["hcl"]!!,
                    eyeColour = it["ecl"]!!,
                    passportId = it["pid"]!!,
                    countryId = it["cid"]
                )
            }
    }

    /**
     * byr (Birth Year)
    iyr (Issue Year)
    eyr (Expiration Year)
    hgt (Height)
    hcl (Hair Color)
    ecl (Eye Color)
    pid (Passport ID)
    cid (Country ID)
     */
    data class Passport(
        val birthYear: Int,
        val issueYear: Int,
        val expirationYear: Int,
        val height: String,
        val hairColour: String,
        val eyeColour: String,
        val passportId: String,
        val countryId: String?
    )

    fun part1(input: String) = parseInput(input).count()

}

