package org.malloys.akm.aoc2022.day8

import com.google.common.collect.ImmutableSet
import org.malloys.akm.aoc2022.lib.readInput

data class Coordinate(val x : Int, val y : Int) {
    operator fun plus(dir : Direction) : Coordinate = with(dir) {Coordinate(x = x + dx, y = y + dy)}
}

enum class Direction(val dx : Int, val dy : Int) {
    NORTH(dx = 0, dy = -1),
    EAST(dx = 1, dy = 0),
    SOUTH(dx = 0, dy = 1),
    WEST(dx = -1, dy = 0),
}

enum class ScanPlan(val startPos : Coordinate, val scanDir : Direction, val doneDir : Direction) {
    N_TO_S(startPos = Coordinate(0, 0), scanDir = Direction.SOUTH, doneDir = Direction.EAST),
    E_TO_W(startPos = Coordinate(x = 1, y = 0), scanDir = Direction.WEST, doneDir = Direction.SOUTH),
    S_TO_N(startPos = Coordinate(x = 0, y = 1), scanDir = Direction.NORTH, doneDir = Direction.EAST),
    W_TO_E(startPos = Coordinate(0, 0), scanDir = Direction.EAST, doneDir = Direction.SOUTH)
}

data class Forest(val trees : List<List<Int>>) {
    val height = trees.size
    val width = trees[0].size
}

fun main() {
    val forest = Forest(readInput(8).map {row ->
        row.map {it.digitToInt()}
    })
    println("Part 1: ${part1(forest)}")
    println("Part 2: ${part2(forest)}")
}

fun scanWhileValid(forest : Forest, start : Coordinate, dir : Direction) : Sequence<Coordinate> =
    with(forest) {
        val xBounds = 0 until width
        val yBounds = 0 until height
        generateSequence(start) {(it + dir).takeIf {(x, y) -> x in xBounds && y in yBounds}}
    }

fun scanOrder(forest : Forest, plan : ScanPlan) : Sequence<Sequence<Coordinate>> =
    with(forest) {
        val startPos = with(plan.startPos) {
            Coordinate(x = x * (width - 1), y = y * (height - 1))
        }

        val rowStarts = scanWhileValid(forest, startPos, plan.doneDir)
        rowStarts.map {scanWhileValid(forest, it, plan.scanDir)}
    }

fun part1(forest : Forest) : Int {
    val visibles = ImmutableSet.builder<Coordinate>()
    val scans = ScanPlan.values().asSequence().flatMap {plan ->
        scanOrder(forest, plan)
    }
    for (scan in scans) {
        var height = -1
        for (coordinate in scan) {
            with(coordinate) {
                val newHeight = forest.trees[y][x]
                if (newHeight > height) {
                    height = newHeight
                    visibles.add(coordinate)
                }
            }
        }
    }
    return visibles.build().size
}

fun part2(forest : Forest) : Int {
    val allCoords = with(forest) {
        (0 until height).flatMap {y -> (0 until width).map {x -> Coordinate(x, y)}}
    }

    return allCoords.maxOf {coord ->
        Direction.values().asSequence().map {directedScenicScore(it, coord, forest)}.reduce(Int::times)
    }
}

fun directedScenicScore(dir : Direction, start : Coordinate, forest : Forest) : Int {
    fun lookup(coord : Coordinate) : Int = forest.trees[coord.y][coord.x]
    val height = lookup(start)
    val coords = scanWhileValid(forest, start, dir)
    var count = 0
    for (coord in coords.drop(1)) {
        count++
        if (lookup(coord) >= height) {
            break
        }
    }
    return count
}
