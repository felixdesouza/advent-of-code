package aoc2020

object Day25 {

    val input = 5290733 to 15231938

    val modulus = 20201227
    // card specific loop size, door different

    fun transform(subjectNumber: Long, loopSize: Int): Int {
        return generateSequence(1L) { prev -> (prev * subjectNumber) % modulus }
                .map { it.toInt() }
                .drop(loopSize).first()
    }

    fun findLoopSize(subjectNumber: Int, publicKey: Int): Int {
        return generateSequence(1L) { prev -> (prev * subjectNumber) % modulus }
                .map { it.toInt() }
                .withIndex()
                .find { it.value == publicKey }!!.index
    }

    fun handshake(cardLoopSize: Int, doorLoopSize: Int): Long {
        val cardPublicKey = transform(7, cardLoopSize)
        val doorPublicKey = transform(7, doorLoopSize)

        println("cardPublicKey = ${cardPublicKey}")
        println("doorPublicKey = ${doorPublicKey}")

        val encryptionKeyByCard = transform(doorPublicKey.toLong(), cardLoopSize)
        val encryptionKeyByDoor = transform(cardPublicKey.toLong(), doorLoopSize)

        println("$encryptionKeyByCard, $encryptionKeyByDoor")

        return 0L
    }

    fun part1(cardPublicKey: Int, doorPublicKey: Int): Int {
        val cardLoopSize = findLoopSize(7, cardPublicKey)
        return transform(doorPublicKey.toLong(), cardLoopSize)
    }
}

fun main() {
    println(Day25.part1(5764801, 17807724))
    println(Day25.part1(Day25.input.first, Day25.input.second))
}