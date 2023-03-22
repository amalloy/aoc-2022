package org.malloys.akm.aoc2022.day6

import com.google.common.collect.HashMultiset
import com.google.common.collect.Streams.mapWithIndex
import org.malloys.akm.aoc2022.lib.readInput
import java.util.Optional

fun main() {
    val packets = readInput(6)[0]
    println("Size: ${packets.length}")
    println("Part 1: ${startOffset(packets, 4)}")
}

fun startOffset(packets : String, syncPacketSize : Int) : Int {
    val counts = HashMultiset.create<Char>(syncPacketSize)
    val buffer = ArrayDeque<Char>(syncPacketSize)
    fun consumeAndCheckForSync(c : Char) : Boolean {
        if (buffer.size == syncPacketSize) {
            counts.remove(buffer.removeFirst())
        }
        buffer.addLast(c)
        counts.add(c)
        return counts.elementSet().size == syncPacketSize
    }

    return packets.chars().mapToObj {consumeAndCheckForSync(it.toChar())}.let {
        mapWithIndex(it) {done, i ->
            when {
                done -> Optional.of(i.toInt())
                else -> Optional.empty()
            }
        }.flatMap(Optional<Int>::stream)
            .findFirst().orElseThrow {RuntimeException("No sync sequence")} + 1
    }
}