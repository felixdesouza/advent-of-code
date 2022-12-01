package common

import kotlin.math.abs

data class Circular(val buffer: IntArray, var head: Int) {
    val size = buffer.size

    fun first() = head

    fun cycle(i: Int): Circular {
        val newHead = getAtIndex(i)
        return copy(head = newHead)
    }

    fun withHead(head: Int): Circular {
        return copy(head = head)
    }

    fun getAtIndex(i: Int): Int {
        val norm = i % size
        val forward = ((norm + size) % size)

        var curr = head
        for (unused in (0 until abs(forward))) {
            curr = buffer[curr]
        }

        return curr
    }

    fun subList(n: Int): List<Int> {
        return generateSequence(head) { buffer[it] }.take(n).toList()
    }

    fun toList(): List<Int> {
        return subList(size)
    }
}