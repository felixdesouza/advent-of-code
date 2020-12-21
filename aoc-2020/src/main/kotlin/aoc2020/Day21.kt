package aoc2020

import com.google.common.collect.Sets
import common.readLines

object Day21 {

    val input = readLines("/aoc2020/day21.txt").map { Line.parseLine(it) }

    val testInput = """
        mxmxvkd kfcds sqjhc nhms (contains dairy, fish)
        trh fvjkl sbzzf mxmxvkd (contains dairy)
        sqjhc fvjkl (contains soy)
        sqjhc mxmxvkd sbzzf (contains fish)
    """.trimIndent().lines().map { Line.parseLine(it) }

    data class Line(val ingredients: Set<String>, val allergens: Set<String>) {
        companion object {
            fun parseLine(line: String): Line {
                return when {
                    "(contains" in line -> {
                        val (ingredientsString, allergensString) = line.split(" (contains ", ")")
                        Line(ingredientsString.split(" ").toSet(), allergensString.split(", ").toSet())

                    }
                    else -> Line(line.split(" ").toSet(), emptySet())
                }
            }
        }
    }

    data class Assignments(val assignments: Map<String, String>, val deferred: Map<String, Set<String>>)

    fun assignTick(assignments: Assignments): Assignments {
        return assignments.deferred.entries
                .fold(Assignments(assignments.assignments, emptyMap())) { (allergenToIngredient, deferred), (allergen, candidates) ->
                    val assignedIngredients = allergenToIngredient.values.toSet()
                    val filteredCandidates = candidates - assignedIngredients

                    if (filteredCandidates.size > 1) {
                        Assignments(allergenToIngredient, deferred + (allergen to filteredCandidates))
                    } else {
                        Assignments(allergenToIngredient + (allergen to filteredCandidates.first()), deferred)
                    }
                }
    }

    fun part1(lines: List<Line>): Long {
        val ingredientsWithAllergies = calculateAllergens(lines)

        val counts = lines.flatMap { it.ingredients.filterNot { it in ingredientsWithAllergies } }
                .groupingBy({it}).eachCount()
        return counts.values.map { it.toLong() }.sum()
    }

    private fun calculateAllergens(lines: List<Line>): Map<String, String> {
        val linesByAllergens = lines.flatMap { line -> line.allergens.map { allergen -> allergen to line } }
                .groupBy({ it.first }, { it.second })

        val reduced = linesByAllergens
                .mapValues { it.value.map { it.ingredients }.reduce { acc, next -> Sets.intersection(acc, next) } }
                .entries.sortedBy { it.value.size }
                .map { (a, b) -> a to b }
                .toMap()

        return generateSequence(Assignments(emptyMap(), reduced)) { assignTick(it) }
                .windowed(2)
                .first { (a, b) -> a == b }
                .first()
                .assignments
                .map { (allergen, ingredient) -> ingredient to allergen }
                .toMap()
    }

    fun part2(lines: List<Line>): String {
        val ingredientsWithAllergies = calculateAllergens(lines)
        return ingredientsWithAllergies.entries.sortedBy { it.value }.map { it.key }.joinToString(",")
    }

}

fun main() {
    println(Day21.part1(Day21.testInput))
    println(Day21.part2(Day21.input))
}