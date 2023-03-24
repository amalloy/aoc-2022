package org.malloys.akm.aoc2022.day9

import com.google.common.collect.ImmutableMultiset
import org.malloys.akm.aoc2022.lib.Coordinate
import org.malloys.akm.aoc2022.lib.Direction
import org.malloys.akm.aoc2022.lib.readInput
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.sign

fun Coordinate.maxMagnitude() = max(abs(x), abs(y))
fun Coordinate.clampMagnitudesTo(limit : Int) = Coordinate(x.sign * limit, y.sign * limit)

data class Motion(val direction : Direction, val magnitude : Int) {
    val asSequenceOfSteps = (0 until magnitude).asSequence().map {direction}
}

data class Rope(val head : Coordinate, val tail : Coordinate)

val motion = Regex("""([LURD]) (\d+)""")
fun parse(line : String) : Motion {
    return motion.matchEntire(line)?.let {match ->
        val (dir, mag) = match.destructured
        Motion(
            when (dir) {
                "L" -> Direction.WEST
                "U" -> Direction.NORTH
                "R" -> Direction.EAST
                "D" -> Direction.SOUTH
                else -> throw RuntimeException("Invalid direction $dir")
            }, mag.toInt()
        )
    } ?: throw RuntimeException("No parse: $line")
}

fun main() {
    val path : List<Motion> = readInput(9).map {parse(it)}
    println("Part 1: ${part1(path)}")
}

fun part1(path : List<Motion>) : Int {
    val ropeLength = 1
    val origin = Coordinate(0, 0)
    var rope = Rope(origin, origin)
    val coordinatesVisitedByTail = ImmutableMultiset.builder<Coordinate>().add(origin)
    for (dir in path.asSequence().flatMap {it.asSequenceOfSteps}) {
        val newHead = rope.head + dir
        val delta = newHead - rope.tail
        val newTail = if (delta.maxMagnitude() <= ropeLength) {
            rope.tail
        } else {
            (rope.tail + delta.clampMagnitudesTo(ropeLength)).also {
                coordinatesVisitedByTail.add(it)
            }
        }
        rope = Rope(newHead, newTail)
    }
    return coordinatesVisitedByTail.build().entrySet().size
}
