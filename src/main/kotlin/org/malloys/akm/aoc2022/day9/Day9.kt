package org.malloys.akm.aoc2022.day9

import com.google.common.collect.ImmutableSet
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
    println("Part 1: ${simulateRope(1, path)}")
    println("Part 2: ${simulateRope(9, path)}")
}

const val ROPE_LENGTH = 1
val origin = Coordinate(0, 0)
fun simulateRope(numKnots : Int, path : List<Motion>) : Int {
    val coordinatesVisitedByTail = ImmutableSet.builder<Coordinate>().add(origin)
    var head = origin
    var knots = (0 until numKnots).map {origin}
    for (dir in path.asSequence().flatMap {it.asSequenceOfSteps}) {
        head += dir
        knots = knots.asSequence().scan(head) {h, t ->
            val delta = h - t
            if (delta.maxMagnitude() <= ROPE_LENGTH) {
                t
            } else {
                t + delta.clampMagnitudesTo(ROPE_LENGTH)
            }
        }.drop(1).toList()
        coordinatesVisitedByTail.add(knots.last())
    }
    return coordinatesVisitedByTail.build().size
}
