package aoc2021

import common.openFile
import common.toBinaryString

object Day16 {

    val input = openFile("/aoc2021/day16.txt").let { Input.parse(it) }

    val testInput = mapOf(
            "8A004A801A8002F478" to 16,
            "620080001611562C8802118E34" to 12,
            "C0015000016115A2E0802F182340" to 23,
            "A0016C880162017C3686B18A3D4780" to 31)

    val testInputPart2 = mapOf(
            "C200B40A82" to 3,
            "04005AC33890" to 54,
            "880086C3E88112" to 7,
            "CE00C43D881120" to 9,
            "D8005AC2A8F0" to 1,
            "F600BC2D8F" to 0,
            "9C005AC2F8F0" to 0,
            "9C0141080250320F1802104A08" to 1)

    data class Input(val bitSet: List<Boolean>) {
        companion object {
            fun parse(input: String): Input {
                return input
                        .map { Integer.parseInt(it.toString(), 16) }
                        .map { it.toBinaryString(4) }
                        .joinToString("")
                        .map { it == '1' }
                        .let { Input(it) }
            }
        }

        private fun subInput(from: Int, to: Int): Input {
            return Input(bitSet.subList(from, to))
        }

        private fun readBits(start: Int, length: Int): Long {
            return bitSet.subList(start, start + length)
                    .joinToString("") { if (it) "1" else "0" }
                    .let { java.lang.Long.parseLong(it, 2) }
        }

        private fun split(index: Int): Pair<Input, Input> {
            return subInput(0, index) to subInput(index, bitSet.size)
        }

        fun parsePacket(): Pair<Packet, Input> {
            val packetVersion = readBits(0, 3).toInt()

            return when (val packetId = readBits(3, 3).toInt()) {
                4 -> {
                    val literals = mutableListOf<Long>()

                    var i = 6
                    do {
                        val value = readBits(i, 5)
                        val element = value.and(1L.shl(4) - 1)
                        literals.add(element)
                        i += 5
                    } while (value.and(1L.shl(4)) > 0)

                    val literal = literals.joinToString("") { it.toBinaryString(4) }.let { java.lang.Long.parseLong(it, 2) }

                    LiteralPacket(packetVersion, packetId, literal) to subInput(i, bitSet.size)
                }
                else -> {
                    val lengthType = readBits(6, 1)
                    val subInput = subInput(7, bitSet.size)
                    if (lengthType == 0L) {
                        val totalLengthInBits = subInput.readBits(0, 15).toInt()
                        val postLengthParse = subInput.subInput(15, subInput.bitSet.size)
                        val (packetsToParse, remainder) = postLengthParse.split(totalLengthInBits)
                        var remainingPackets = packetsToParse
                        val subPackets = mutableListOf<Packet>()

                        do {
                            val (packet, input) = remainingPackets.parsePacket()
                            remainingPackets = input
                            subPackets.add(packet)
                        } while (remainingPackets.bitSet.isNotEmpty())

                        OperatorPacket(packetVersion, packetId, subPackets) to remainder
                    } else {
                        val numberOfSubPackets = subInput.readBits(0, 11).toInt()
                        val remainingPackets = subInput.subInput(11, subInput.bitSet.size)
                        (0 until numberOfSubPackets).fold(remainingPackets to listOf<Packet>()) { acc, i ->
                            val (remainingInput, subPackets) = acc
                            val (packet, newRemainingInput) = remainingInput.parsePacket()
                            newRemainingInput to subPackets + packet
                        }.let { (input, subPackets) -> OperatorPacket(packetVersion, packetId, subPackets) to input }
                    }
                }
            }

        }
    }

    fun part1(input: Input): Long {
        val (packet, _) = input.parsePacket()
        return packet.versionSum().toLong()
    }

    fun part2(input: Input): Long {
        val (packet, _) = input.parsePacket()
        return packet.evaluate()
    }

    @JvmStatic
    fun main(args: Array<String>) {
        testInput.mapKeys { (string, _) ->
            val (packet, _) = Input.parse(string).parsePacket()
            packet.versionSum()
        }.filter { (actual, expected) -> actual != expected }.also { println(it) }

        println(part1(input))

        testInputPart2.mapKeys { (string, _) ->
            val (packet, _) = Input.parse(string).parsePacket()
            packet.evaluate()
        }.filter { (actual, expected) -> actual.toInt() != expected }.also { println(it) }

        println(part2(input))
    }

}

sealed class Packet(open val packetVersion: Int, open val packetType: Int) {
    abstract fun versionSum(): Int
    abstract fun evaluate(): Long
}

data class LiteralPacket(
        override val packetVersion: Int,
        override val packetType: Int,
        val literal: Long) : Packet(packetVersion, packetType) {
    override fun versionSum(): Int {
        return packetVersion
    }

    override fun evaluate(): Long {
        return literal
    }
}

data class OperatorPacket(
        override val packetVersion: Int,
        override val packetType: Int,
        val subPackets: List<Packet>
) : Packet(packetVersion, packetType) {
    override fun versionSum(): Int {
        return packetVersion + subPackets.sumBy { it.versionSum() }
    }

    override fun evaluate(): Long {
        val subPacketEvaluation = subPackets.map { it.evaluate() }
        return when (packetType) {
            0 -> subPacketEvaluation.sum()
            1 -> subPacketEvaluation.reduce { acc, next -> acc * next }
            2 -> subPacketEvaluation.min()!!
            3 -> subPacketEvaluation.max()!!
            5 -> {
                val (a, b) = subPacketEvaluation
                if (a > b) 1 else 0
            }
            6 -> {
                val (a, b) = subPacketEvaluation
                if (a < b) 1 else 0
            }
            7 -> {
                val (a, b) = subPacketEvaluation
                if (a == b) 1 else 0
            }
            else -> throw AssertionError("unexpected packet type")
        }
    }
}
