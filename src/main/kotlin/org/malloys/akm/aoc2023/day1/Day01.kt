package org.malloys.akm.aoc2023.day1

import com.google.common.collect.Comparators
import org.malloys.akm.aoc2023.lib.splitAtBlankLines
import org.malloys.akm.aoc2023.lib.readInput

data class Elf(val inventory: List<Int>)

fun main() {
    val elves = parseElves(readInput(1).splitAtBlankLines())
    println("Part 1: ${part1(elves)}")
    println("Part 2: ${part2(elves)}")
}

fun part1(elves: List<Elf>): Int = elves.asSequence().map { it.inventory.sum() }.max()

fun part2(elves: List<Elf>): Int =
    elves.stream().map { it.inventory.sum() }
        .collect(Comparators.greatest(3, naturalOrder()))
        .sum()

private fun parseElves(inputLines: List<List<String>>): List<Elf> =
    inputLines.map { lines -> Elf(lines.map(String::toInt)) }
