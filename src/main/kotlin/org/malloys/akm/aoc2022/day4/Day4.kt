package org.malloys.akm.aoc2022.day4

import com.google.common.collect.Range
import org.malloys.akm.aoc2022.lib.readInput

typealias Assignment = Pair<Range<Int>, Range<Int>>

fun main() {
    val rangePair = Regex("""(\d+)-(\d+),(\d+)-(\d+)""")
    val assignments : List<Assignment> = readInput(4).map {
        val result = rangePair.matchEntire(it) ?: throw RuntimeException("Bad input $it")
        val (alo, ahi, blo, bhi) = result.destructured
        Assignment(Range.closed(alo.toInt(), ahi.toInt()), Range.closed(blo.toInt(), bhi.toInt()))
    }

    println("Part 1: ${part1(assignments)}")
    println("Part 2: ${part2(assignments)}")
}

fun part1(assignments : List<Assignment>) : Int =
    assignments.count {it.first.encloses(it.second) || it.second.encloses(it.first)}

fun part2(assignments : List<Assignment>) : Int =
    assignments.count {it.first.isConnected(it.second)}
