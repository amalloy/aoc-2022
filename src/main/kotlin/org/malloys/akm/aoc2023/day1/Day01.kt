package org.malloys.akm.aoc2023.day1

import com.google.common.collect.Comparators
import org.malloys.akm.aoc2023.lib.readInput

data class Elf(val inventory: List<Int>)

fun main() {
    val inputLines = readInput(1).iterator()
    val elves = parseElves(inputLines)
    println("Part 1: ${part1(elves)}")
    println("Part 2: ${part2(elves)}")
}

fun part1(elves: List<Elf>): Int = elves.asSequence().map { it.inventory.sum() }.max()

fun part2(elves: List<Elf>): Int =
    elves.stream().map { it.inventory.sum() }
        .collect(Comparators.greatest(3, naturalOrder()))
        .sum()

private fun parseElves(inputLines: Iterator<String>): List<Elf> {
    val elves = buildList {
        fun nextElf(): Elf = Elf(generateSequence {
            val line: String = if (inputLines.hasNext()) {
                inputLines.next()
            } else {
                ""
            }
            if (line.isEmpty()) {
                return@generateSequence null
            }
            line.toInt()
        }.toList())
        while (inputLines.hasNext()) {
            add(nextElf())
        }
    }
    return elves
}
