package org.malloys.akm.aoc2022.day11

import com.google.common.collect.Comparators
import com.google.common.collect.ImmutableList
import org.malloys.akm.aoc2022.lib.readInput
import java.util.ArrayDeque

typealias Item = Long
typealias MonkeyIndex = Int

data class Monkey(
    val items : ArrayDeque<Item>,
    val operation : (Item) -> Item,
    val factor : Long,
    val ifTrue : MonkeyIndex,
    val ifFalse : MonkeyIndex,
    val worryReductionFactor : Long = 3L,
    var itemsHandled : Long = 0
) {
    fun doTurn() : List<Pair<MonkeyIndex, Item>> {
        val ret = ImmutableList.builder<Pair<MonkeyIndex, Item>>()
        while (items.isNotEmpty()) {
            val new = (operation(items.removeFirst()) / worryReductionFactor)
            val recipient = if (new % factor == 0L) {
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
        items.splitToSequence(", ").map {it.toLong()}.toCollection(ArrayDeque())
    } ?: throw RuntimeException("No parse: $s")

val OPERATION = Regex("""Operation: new = old (.) (old|\d+)""")
fun parseOperation(s : String) : (Item) -> Item {
    return OPERATION.matchEntire(s)?.destructured?.let {(op, arg) ->
        val argf : (Item) -> Item = when (arg) {
            "old" -> {x -> x}
            else -> arg.toLong().let {n -> {_ -> n}}
        }
        val opf : (Item, Item) -> Item = when (op) {
            "+" -> Item::plus
            "*" -> Item::times
            else -> throw RuntimeException("No operator $op")
        }
        {x -> opf(x, argf(x))}
    } ?: throw RuntimeException("No parse: $s")
}

val FACTOR = Regex("""Test: divisible by (\d+)""")
fun parseFactor(s : String) : Long =
    FACTOR.matchEntire(s)?.destructured?.let {(d) ->
        d.toLong()
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
    println("Part 2: ${simulateRounds(10000, monkeys.map {it.copy(worryReductionFactor = 1)})}")
}

fun simulateRounds(numRounds : Int, initMonkeys : List<Monkey>) : Long {
    val lcm = initMonkeys.stream().map {it.factor}.reduce(Long::times).orElseThrow()
    val monkeys = initMonkeys.map {it.copy(items = it.items.clone())}
    repeat(numRounds) {
        for (monkey in monkeys) {
            for ((recipient, item) in monkey.doTurn()) {
                monkeys[recipient].items.addFirst(item % lcm)
            }
        }
    }
    return monkeys.stream()
        .map {it.itemsHandled}
        .collect(Comparators.greatest(2, naturalOrder<Long>()))
        .reduce(Long::times)
}
