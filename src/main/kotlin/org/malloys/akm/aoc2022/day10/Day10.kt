package org.malloys.akm.aoc2022.day10

import org.malloys.akm.aoc2022.lib.readInput

sealed interface Instruction
object Noop : Instruction
data class Addx(val delta : Int) : Instruction

val ADD_X = Regex("""addx (-?\d+)""")
fun parse(line : String) =
    when (line) {
        "noop" -> Noop
        else -> ADD_X.matchEntire(line)?.destructured?.let {(delta) ->
            Addx(delta.toInt())
        } ?: throw RuntimeException("No parse: $line")
    }

fun main() {
    val program = readInput(10).map {parse(it)}
    println("Part 1: ${part1(program)}")
}

fun part1(program : List<Instruction>) : Int {
    val tickChanges = program.asSequence().flatMap {instruction ->
        when (instruction) {
            is Addx -> sequenceOf(0, instruction.delta)
            Noop -> sequenceOf(0)
        }
    }
    val xs = sequence {
        yield(0)
        yieldAll(tickChanges.scan(1, Int::plus))
    }
    return xs.mapIndexed {cycleNum, x -> x * cycleNum}.filterIndexed {cycleNum, x -> (cycleNum % 40) == 20}.sum()
}
