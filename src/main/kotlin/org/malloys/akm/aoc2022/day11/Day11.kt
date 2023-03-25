package org.malloys.akm.aoc2022.day11

import org.malloys.akm.aoc2022.lib.readInput
import java.util.ArrayDeque
import java.util.Queue

typealias Item = Int
typealias MonkeyIndex = Int

data class Monkey(
    val items : Queue<Item>,
    val operation : (Item) -> Item,
    val factor : Int,
    val ifTrue : MonkeyIndex,
    val ifFalse : MonkeyIndex
)

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
fun parseItems(s : String) : Queue<Item> =
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
    println(monkeys)
}
