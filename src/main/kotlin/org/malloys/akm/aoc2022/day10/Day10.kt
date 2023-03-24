package org.malloys.akm.aoc2022.day10

import org.malloys.akm.aoc2022.lib.readInput
import kotlin.math.abs

sealed interface Instruction
object Noop : Instruction
data class Addx(val delta : Int) : Instruction

data class RegisterInfo(val cycleNum : Int, val x : Int)

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
    println("Part 2: \n${part2(program)}")
}

private fun registerInfoByCycle(program : List<Instruction>) : Sequence<RegisterInfo> {
    val tickChanges = program.asSequence().flatMap {instruction ->
        when (instruction) {
            is Addx -> sequenceOf(0, instruction.delta)
            Noop -> sequenceOf(0)
        }
    }
    return tickChanges
        .scan(1, Int::plus)
        .mapIndexed {cycleNum, x -> RegisterInfo(cycleNum = cycleNum + 1, x = x)}
}

fun part1(program : List<Instruction>) : Int =
    registerInfoByCycle(program)
        .filter {reg -> with(reg) {(cycleNum % 40) == 20}}
        .map {reg -> with(reg) {cycleNum * x}}.sum()

fun part2(program : List<Instruction>) =
    registerInfoByCycle(program).map {reg ->
        with(reg) {
            val columnBeingDrawn = (cycleNum - 1) % 40
            abs(x - columnBeingDrawn) <= 1
        }
    }.map {
        when (it) {
            true -> "#"
            false -> "."
        }
    }.chunked(40).map {it.joinToString(separator = "")}.joinToString(separator = "\n")
