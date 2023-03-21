package org.malloys.akm.aoc2023.day3

import com.google.common.collect.ImmutableMultiset
import com.google.common.collect.Iterables
import com.google.common.collect.Multiset
import com.google.common.collect.Multisets
import org.malloys.akm.aoc2023.lib.readInput

data class ItemType(val label: Char) {
    val priority = when (label) {
        in 'a'..'z' -> (label - 'a') + 1
        in 'A'..'Z' -> (label - 'A') + 1 + 26
        else -> throw RuntimeException("Bad label $label")
    }
}

data class Rucksack(val left: ImmutableMultiset<ItemType>, val right: ImmutableMultiset<ItemType>) {
    val overlap: Set<Multiset.Entry<ItemType>> = Multisets.intersection(left, right).entrySet()
}

fun String.toMultiSet(): ImmutableMultiset<ItemType> {
    val builder = ImmutableMultiset.Builder<ItemType>()
    for (x in chars()) {
        builder.add(ItemType(x!!.toChar()))
    }
    return builder.build()
}

fun String.toRucksack(): Rucksack {
    val size = length / 2
    return Rucksack(substring(0, size).toMultiSet(), substring(size, length).toMultiSet())
}

fun main() {
    val rucksacks = readInput(3).map { it.toRucksack() }
    println("Part 1: ${part1(rucksacks)}")
    println("Part 2: ${part2(rucksacks)}")
}

fun part1(rucksacks: List<Rucksack>): Int {
    return rucksacks.sumOf {
        it.overlap.stream().findAny().orElseThrow { RuntimeException(it.toString()) }.element.priority
    }
}

fun part2(rucksacks: List<Rucksack>): Int =
    rucksacks.chunked(3).sumOf { group ->
        val contents = group.stream().map { it.left.elementSet().union(it.right.elementSet()) }.toList()
        val commonType = contents[0].intersect(contents[1]).intersect(contents[2])
        Iterables.getOnlyElement(commonType).priority
    }