package aoc2021

import common.readLines

object Day8 {
    fun parseInput(line: String): IO {
        val (input, output) = line.split("|")
                .map { it.split(" ").map { it.trim() }.filter { it.isNotBlank() } }
        return IO(input, output)
    }

    data class IO(val input: List<String>, val output: List<String>) {
        private val one = input.find { it.length == 2 }!!.toSet()
        private val four = input.find { it.length == 4 }!!.toSet()
        private val seven = input.find { it.length == 3 }!!.toSet()
        private val eight = input.find { it.length == 7 }!!.toSet()

        fun deduce(): Int {
            val a = (seven - one).first()
            val g = input.map { it.toSet() - seven - four }.first { it.size == 1 }.first()
            val e = (eight - seven - four - g).first()
            val d = input.map { it.toSet() - seven - g }.first{ it.size == 1}.first()
            val b = (four - one - d).first()
            val f  = input.map { it.toSet() - a - d - g - b }.first { it.size == 1}.first()
            val c = (eight - a - g - e - b - d - f).first()

            val mapping = mapOf(
                    one to 1,
                    four to 4,
                    seven to 7,
                    eight to 8,
                    setOf(a, b, c, e, f, g) to 0,
                    setOf(a, c, d, e, g) to 2,
                    setOf(a, c, d, f, g) to 3,
                    setOf(a, b, d, f, g) to 5,
                    setOf(a, b, d, e, f, g) to 6,
                    setOf(a, b, c, d, f, g) to 9
            )

            return output.map { mapping[it.toSet()]!! }.joinToString(separator = "") { it.toString() }.let { Integer.parseInt(it) }
        }
    }

    val input = readLines("/aoc2021/day8.txt")
            .map { parseInput(it) }

    val testInput = """
        be cfbegad cbdgef fgaecd cgeb fdcge agebfd fecdb fabcd edb | fdgacbe cefdb cefbgd gcbe
        edbfga begcd cbg gc gcadebf fbgde acbgfd abcde gfcbed gfec | fcgedb cgb dgebacf gc
        fgaebd cg bdaec gdafb agbcfd gdcbef bgcad gfac gcb cdgabef | cg cg fdcagb cbg
        fbegcd cbd adcefb dageb afcb bc aefdc ecdab fgdeca fcdbega | efabcd cedba gadfec cb
        aecbfdg fbg gf bafeg dbefa fcge gcbea fcaegb dgceab fcbdga | gecf egdcabf bgf bfgea
        fgeab ca afcebg bdacfeg cfaedg gcfdb baec bfadeg bafgc acf | gebdcfa ecba ca fadegcb
        dbcfg fgd bdegcaf fgec aegbdf ecdfab fbedc dacgb gdcebf gf | cefg dcbef fcge gbcadfe
        bdfegc cbegaf gecbf dfcage bdacg ed bedf ced adcbefg gebcd | ed bcgafe cdgba cbgef
        egadfb cdbfeg cegd fecab cgb gbdefca cg fgcdab egfdb bfceg | gbdfcae bgc cg cgb
        gcafb gcf dcaebfg ecagb gf abcdeg gaef cafbge fdbac fegbdc | fgae cfgab fg bagce
    """.trimIndent()
            .lines()
            .map { parseInput(it) }

    fun part1(input:List<IO>): Int {
        return input.map { it.output.count { it.length in setOf(2, 4, 7, 3) } }.sum()
    }

    fun part2(input: List<IO>): Int {
        return input.asSequence().map { it.deduce() }.sum()
    }

    @JvmStatic
    fun main(args: Array<String>) {
        println(part1(testInput))
        println(part1(input))
        println(part2(testInput))
        println(part2(input))
    }
}
