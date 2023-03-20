package org.malloys.akm.aoc2023.day2

import org.malloys.akm.aoc2023.lib.readInput

enum class Shape(val score: Int) {
    ROCK(1) {
        override fun beats(other: Shape): Boolean = other == SCISSORS
    },
    PAPER(2) {
        override fun beats(other: Shape): Boolean = other == ROCK
    },
    SCISSORS(3) {
        override fun beats(other: Shape): Boolean = other == PAPER
    };

    abstract fun beats(other: Shape): Boolean
}

enum class Outcome(val score: Int) { WIN(6), DRAW(3), LOSE(0) }
enum class Left { A, B, C }
enum class Right { X, Y, Z }
data class Strategy(val left: Left, val right: Right)

fun outcomeForP2(p1: Shape, p2: Shape): Outcome {
    if (p1.beats(p2)) {
        return Outcome.LOSE
    }
    if (p2.beats(p1)) {
        return Outcome.WIN
    }
    return Outcome.DRAW
}

fun scoreForP2(p1: Shape, p2: Shape): Int = outcomeForP2(p1, p2).score + p2.score

fun main() {
    val regex = Regex("([ABC]) ([XYZ])")
    val guide = readInput(2).map {
        val match = regex.matchEntire(it) ?: throw RuntimeException("Bad input: $it")
        val (left, right) = match.destructured
        Strategy(Left.valueOf(left), Right.valueOf(right))
    }
    print(guide)

}