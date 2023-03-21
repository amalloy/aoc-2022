package org.malloys.akm.aoc2023.day4

import com.google.common.collect.Range
import org.malloys.akm.aoc2023.lib.readInput

typealias Assignment = Pair<Range<Int>, Range<Int>>

fun main() {
    val rangePair = Regex("""(\d+)-(\d+),(\d+)-(\d+)""")
    val assignments: List<Assignment> = readInput(4).map {
        val result = rangePair.matchEntire(it) ?: throw RuntimeException("Bad input $it")
        val (alo, ahi, blo, bhi) = result.destructured
        Assignment(Range.closed(alo.toInt(), ahi.toInt()), Range.closed(blo.toInt(), bhi.toInt()))
    }

    println("Part 1: ${part1(assignments)}")
}

fun part1(assignments: List<Assignment>): Int =
    assignments.count { it.first.encloses(it.second) || it.second.encloses(it.first) }
