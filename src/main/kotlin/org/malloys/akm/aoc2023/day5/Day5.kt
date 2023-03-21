package org.malloys.akm.aoc2023.day5

import org.malloys.akm.aoc2023.lib.readInput
import java.util.ArrayDeque as ArrayDeque

typealias Stack = ArrayDeque<Char>

data class Move(val quantity: Int, val source: Int, val destination: Int)
data class Input(val stacks: List<Stack>, val moves: List<Move>)

fun main() {
    val input = parse(readInput(5))
    println("Part 1: ${part1(input)}")
}

fun part1(input: Input): String {
    val stacks = input.stacks.map { it.clone() }
    for ((quantity, source, destination) in input.moves) {
        repeat(quantity) {stacks[source - 1].pollLast().let { stacks[destination - 1].addLast(it) }}
    }
    return stacks.map { it.pollLast() }.joinToString(separator = "")
}

fun parse(input: List<String>) = Input(parseStacks(input), parseMoves(input))

private const val NUM_STACKS = 9
fun parseStacks(input: List<String>): List<Stack> {
    val crossSection = Regex("""(?:(?:\[[A-Z]]|\s{3})\s?){0,9}""")
    val stacks = List(NUM_STACKS) { Stack(ArrayDeque()) }
    input.filter {
        crossSection.matches(it)
    }.flatMap { parseRow(it) }.forEach { (idx, label) ->
        stacks[idx].addFirst(label)
    }
    return stacks
}

private val crate = Regex("""\[([A-Z])]\s?""")
fun parseRow(row: String): List<Pair<Int, Char>> {
    return row.chunked(4).flatMapIndexed { i, s ->
        val match = crate.matchEntire(s) ?: return@flatMapIndexed emptyList()
        val (label) = match.destructured
        listOf(Pair(i, label[0]))
    }
}

fun parseMoves(input: List<String>): List<Move> {
    val move = Regex("""move (\d+) from (\d) to (\d)""")
    return input.flatMap {
        val match = move.matchEntire(it) ?: return@flatMap emptyList()
        val (quantity, source, destination) = match.destructured
        listOf(Move(quantity.toInt(), source.toInt(), destination.toInt()))
    }
}
