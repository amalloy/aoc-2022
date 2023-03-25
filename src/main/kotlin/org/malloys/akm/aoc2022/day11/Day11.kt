package org.malloys.akm.aoc2022.day11

import com.google.common.collect.Comparators
import com.google.common.collect.ImmutableList
import org.malloys.akm.aoc2022.lib.readInput
import java.util.ArrayDeque

typealias Item = Int
typealias MonkeyIndex = Int

data class Monkey(
    val items : ArrayDeque<Item>,
    val operation : (Item) -> Item,
    val factor : Int,
    val ifTrue : MonkeyIndex,
    val ifFalse : MonkeyIndex,
) {
    var itemsHandled : Int = 0
    fun doTurn() : List<Pair<MonkeyIndex, Item>> {
        val ret = ImmutableList.builder<Pair<MonkeyIndex, Item>>()
        while (items.isNotEmpty()) {
            val new = operation(items.removeFirst()) / 3
            val recipient = if (new % factor == 0) {
                ifTrue
            } else {
                ifFalse
            }
            ret.add(Pair(recipient, new))
            itemsHandled++
        }
        return ret.build()
    }
}

fun parseMonkey(lines : List<String>) : Monkey {
    return Monkey(
        parseItems(lines[1].trimIndent()),
        parseOperation(lines[2].trimIndent()),
        parseFactor(lines[3].trimIndent()),
        parseIfTrue(lines[4].trimIndent()),
        parseIfFalse(lines[5].trimIndent()),
    )
}

val ITEMS = Regex("""Starting items: ((?:\d+(?:, )?)+)""")
fun parseItems(s : String) : ArrayDeque<Item> =
    ITEMS.matchEntire(s)?.destructured?.let {(items) ->
        items.splitToSequence(", ").map {it.toInt()}.toCollection(ArrayDeque())
    } ?: throw RuntimeException("No parse: $s")

val OPERATION = Regex("""Operation: new = old (.) (old|\d+)""")
fun parseOperation(s : String) : (Item) -> Item {
    return OPERATION.matchEntire(s)?.destructured?.let {(op, arg) ->
        val argf : (Item) -> Item = when (arg) {
            "old" -> {x -> x}
            else -> arg.toInt().let {n -> {_ -> n}}
        }
        val opf : (Item, Item) -> Item = when (op) {
            "+" -> Int::plus
            "*" -> Int::times
            else -> throw RuntimeException("No operator $op")
        }
        {x -> opf(x, argf(x))}
    } ?: throw RuntimeException("No parse: $s")
}

val FACTOR = Regex("""Test: divisible by (\d+)""")
fun parseFactor(s : String) : Int =
    FACTOR.matchEntire(s)?.destructured?.let {(d) ->
        d.toInt()
    } ?: throw RuntimeException("No parse: $s)")

val COND = Regex("""If (\w+): throw to monkey (\d+)""")
fun parseCond(cond : String, s : String) : MonkeyIndex {
    return COND.matchEntire(s)?.destructured?.let {(b, n) ->
        n.toInt().takeIf {b == cond}
    } ?: throw RuntimeException("No parse: $s")
}

fun parseIfTrue(s : String) = parseCond("true", s)

fun parseIfFalse(s : String) = parseCond("false", s)

fun main() {
    val monkeys = readInput(11).chunked(7).map {parseMonkey(it)}
    println("Part 1: ${simulateRounds(20, monkeys)}")
}

fun simulateRounds(numRounds : Int, initMonkeys : List<Monkey>) : Int {
    val lcm = initMonkeys.stream().map {it.factor}.reduce(Int::times).orElseThrow()
    val monkeys = initMonkeys.map {it.copy(items = it.items.clone())}
    repeat(numRounds) {
        for (monkey in monkeys) {
            for ((recipient, item) in monkey.doTurn()) {
                monkeys[recipient].items.addFirst(item % lcm)
            }
        }
    }
    return monkeys.stream().map {it.itemsHandled}.collect(Comparators.greatest(2, naturalOrder<Item>()))
        .reduce(Int::times)
}
