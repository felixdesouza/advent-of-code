package aoc2020

import common.openFile
import ru.lanwen.verbalregex.VerbalExpression

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
    ) {
        companion object {
            private val validEyeColours = setOf("amb", "blu", "brn", "gry", "grn", "hzl", "oth")
            private val passportIdRegex = VerbalExpression.regex().digit().count(9).build()
            private val hairColourRegex = VerbalExpression.regex().find("#").range("0", "9", "a", "f").count(6).build()
            private val heightRegex = VerbalExpression.regex()
                .capture().digit().oneOrMore().endCapture()
                .capt().oneOf("cm", "in").endCapt()
                .build()
        }

        fun isValid(): Boolean {
            return birthYear in (1920..2002)
                    && issueYear in (2010..2020)
                    && expirationYear in (2020..2030)
                    && eyeColour in validEyeColours
                    && passportIdRegex.testExact(passportId)
                    && hairColourRegex.testExact(hairColour)
                    && (heightRegex.testExact(height) && when(heightRegex.getText(height, 2)) {
                        "in" -> heightRegex.getText(height, 1).toInt() in (59..76)
                        "cm" -> heightRegex.getText(height, 1).toInt() in (150..193)
                        else -> false
                    })

        }
    }

    fun part1(input: String) = parseInput(input).count()

    fun part2(input: String) = parseInput(input).filter { it.isValid() }.count()

}

