package org.malloys.akm.aoc2023.day5

import org.malloys.akm.aoc2023.lib.readInput
import java.util.ArrayDeque
import java.util.stream.Collectors.joining

typealias Stack = ArrayDeque<Char>

data class Move(val quantity : Int, val source : Int, val destination : Int)
data class Input(val stacks : List<Stack>, val moves : List<Move>)

fun main() {
    val input = parse(readInput(5))
    println("Part 1: ${part1(input)}")
    println("Part 2: ${part2(input)}")
}

fun runCrane(input : Input, applyMove : (Move, List<Stack>) -> Unit) : String {
    val stacks = input.stacks.map {it.clone()}
    input.moves.forEach {applyMove(it, stacks)}
    return stacks.stream().map {it.pollLast().toString()}.collect(joining())
}

fun part1(input : Input) : String {
    return runCrane(input) {(quantity, source, destination), stacks ->
        repeat(quantity) {stacks[source - 1].pollLast().let {stacks[destination - 1].addLast(it)}}
    }
}

fun part2(input : Input) : String {
    return runCrane(input) {(quantity, source, destination), stacks ->
        val buffer = Stack(ArrayDeque(quantity))
        repeat(quantity) {stacks[source - 1].pollLast().let {buffer.addLast(it)}}
        repeat(quantity) {buffer.pollLast().let {stacks[destination - 1].addLast(it)}}
    }
}

fun parse(input : List<String>) = Input(parseStacks(input), parseMoves(input))

private const val NUM_STACKS = 9
fun parseStacks(input : List<String>) : List<Stack> {
    val crossSection = Regex("""(?:(?:\[[A-Z]]|\s{3})\s?){0,9}""")
    val stacks = List(NUM_STACKS) {Stack(ArrayDeque())}
    input.filter {
        crossSection.matches(it)
    }.flatMap {parseRow(it)}.forEach {(idx, label) ->
        stacks[idx].addFirst(label)
    }
    return stacks
}

private val crate = Regex("""\[([A-Z])]\s?""")
fun parseRow(row : String) : List<Pair<Int, Char>> {
    return row.chunked(4).flatMapIndexed {i, s ->
        val match = crate.matchEntire(s) ?: return@flatMapIndexed emptyList()
        val (label) = match.destructured
        listOf(Pair(i, label[0]))
    }
}

fun parseMoves(input : List<String>) : List<Move> {
    val move = Regex("""move (\d+) from (\d) to (\d)""")
    return input.flatMap {
        val match = move.matchEntire(it) ?: return@flatMap emptyList()
        val (quantity, source, destination) = match.destructured
        listOf(Move(quantity.toInt(), source.toInt(), destination.toInt()))
    }
}
