package aoc2021

import com.google.common.collect.Sets
import common.openFile

object Day18 {

    val input = openFile("/aoc2021/day18.txt").let { parse(it) }

    val testInput = """
        [[[0,[4,5]],[0,0]],[[[4,5],[2,6]],[9,5]]]
        [7,[[[3,7],[4,3]],[[6,3],[8,8]]]]
        [[2,[[0,8],[3,4]]],[[[6,7],1],[7,[1,6]]]]
        [[[[2,4],7],[6,[0,5]]],[[[6,8],[2,8]],[[2,1],[4,5]]]]
        [7,[5,[[3,8],[1,4]]]]
        [[2,[2,2]],[8,[8,1]]]
        [2,9]
        [1,[[[9,3],9],[[9,0],[0,7]]]]
        [[[5,[7,4]],7],1]
        [[[[4,2],2],6],[8,7]]
    """.trimIndent()
            .let { parse(it) }

    val testInput3 = """
        [1,1]
        [2,2]
        [3,3]
        [4,4]
        [5,5]
        [6,6]
    """.trimIndent()
            .let { parse(it) }

    val testInput2 = """
        [[[0,[5,8]],[[1,7],[9,6]]],[[4,[1,2]],[[1,4],2]]]
        [[[5,[2,8]],4],[5,[[9,9],0]]]
        [6,[[[6,2],[5,6]],[[7,6],[4,7]]]]
        [[[6,[0,7]],[0,9]],[4,[9,[9,0]]]]
        [[[7,[6,4]],[3,[1,3]]],[[[5,5],1],9]]
        [[6,[[7,3],[3,2]]],[[[3,8],[5,7]],4]]
        [[[[5,4],[7,7]],8],[[8,3],8]]
        [[9,3],[[9,9],[6,[4,9]]]]
        [[2,[[7,7],7]],[[5,8],[[9,3],[0,2]]]]
        [[[[5,2],5],[8,[3,7]]],[[5,[7,5]],[4,4]]]
    """.trimIndent()
            .let { parse(it) }

    fun parse(input: String): List<SnailFishTree> = input.lines().map { parseFish(it) }

    fun parseFish(input: String): SnailFishTree {
        fun helper(chars: List<Char>): Pair<SnailFishTree, List<Char>> {

            return when (val firstChar = chars[0]) {
                in '0'..'9' -> {
                    Leaf((firstChar - '0').toLong()) to chars.subList(1, chars.size)
                }
                '[' -> {
                    val (left, rest) = helper(chars.subList(1, chars.size))
                    val (right, rest2) = helper(rest.drop(1))
                    SnailFishNode(left, right) to rest2.drop(1)
                }

                else -> throw AssertionError("bla")
            }
        }

        return helper(input.map { it }).first
    }

    fun reduce(snailFishTree: SnailFishTree): SnailFishTree {
        fun iterate(tree: SnailFishTree): SnailFishTree? {
            return explode(tree) ?: split(tree)
        }

        return generateSequence(snailFishTree) { iterate(it) }
                .withIndex()
                .map { it.value }
                .last()
    }

    fun explode(tree: SnailFishTree): SnailFishTree? {
        data class LeafBits(val long: Long, val left: Boolean)
        fun helper(t: SnailFishTree, depth: Int): Pair<SnailFishTree?, List<LeafBits>> {
            return when (t) {
                is Leaf -> null to emptyList()
                is SnailFishNode -> {
                    if (depth >= 4) {
                        Leaf(0) to listOf(
                                LeafBits((t.left as Leaf).value, true),
                                LeafBits((t.right as Leaf).value, false))
                    } else {
                        val (newLeft, remainingBits) = helper(t.left, depth + 1)
                        if (newLeft != null) {
                            val (right, rest) = remainingBits.partition { !it.left }
                            val newRight = if (right.isEmpty()) t.right else addToLeftMost(t.right, right.first().long)
                            SnailFishNode(newLeft, newRight) to rest
                        } else {
                            val (newRight, remainingBits) = helper(t.right, depth + 1)
                            if (newRight != null) {
                                val (left, rest) = remainingBits.partition { it.left }
                                val newLeft = if (left.isEmpty()) t.left else addToRightMost(t.left, left.first().long)
                                SnailFishNode(newLeft, newRight) to rest
                            } else {
                                null to emptyList<LeafBits>()
                            }
                        }
                    }
                }
            }
        }

        return helper(tree, 0).first
    }

    fun addToRightMost(tree: SnailFishTree, value: Long): SnailFishTree {
        return when (tree) {
            is Leaf -> Leaf(tree.value + value)
            is SnailFishNode -> tree.copy(right = addToRightMost(tree.right, value))
        }

    }

    fun addToLeftMost(tree: SnailFishTree, value: Long): SnailFishTree {
        return when (tree) {
            is Leaf -> Leaf(tree.value + value)
            is SnailFishNode -> tree.copy(left = addToLeftMost(tree.left, value))
        }
    }

    fun split(tree: SnailFishTree): SnailFishTree? {
        return when (tree) {
            is Leaf -> {
                if (tree.value >= 10) {
                    SnailFishNode(Leaf(tree.value / 2), Leaf(tree.value / 2 + (tree.value % 2)))
                } else {
                    null
                }
            }
            is SnailFishNode -> {
                val newLeft = split(tree.left)
                if (newLeft != null) {
                    SnailFishNode(newLeft, tree.right)
                } else {
                    val newRight = split(tree.right)
                    if (newRight != null) {
                        tree.copy(right = newRight)
                    } else {
                        null
                    }
                }
            }
        }
    }

    fun magnitude(tree: SnailFishTree): Long {
        return when (tree) {
            is Leaf -> tree.value
            is SnailFishNode -> 3* magnitude(tree.left) + 2 * magnitude(tree.right)
        }
    }

    fun part1(input: List<SnailFishTree>): Long {
        val combined = input.reduce { acc, tree -> reduce(acc + tree) }
        println(combined)
        return magnitude(combined)
    }

    fun part2(input: List<SnailFishTree>): Long {
        return Sets.combinations(input.toSet(), 2)
                .map { it.toList() }
                .flatMap { (a, b) -> listOf(reduce(a + b), reduce(b + a)) }
                .map { magnitude(it) }
                .max()!!
    }

    @JvmStatic
    fun main(args: Array<String>) {
        println(part1(input))
        println(part2(input))
    }

}

sealed class SnailFishTree {
    operator fun plus(other: SnailFishTree): SnailFishTree = SnailFishNode(this, other)
    override fun toString(): String = when (this) {
        is Leaf -> "${this.value}"
        is SnailFishNode -> {
            val left = left.toString()
            val right = right.toString()
            "[$left,$right]"
        }
    }
}

data class SnailFishNode(val left: SnailFishTree, val right: SnailFishTree) : SnailFishTree() {
    override fun toString() = super.toString()
}
data class Leaf(val value: Long) : SnailFishTree()
