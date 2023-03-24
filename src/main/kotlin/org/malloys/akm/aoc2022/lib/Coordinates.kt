package org.malloys.akm.aoc2022.lib

data class Coordinate(val x : Int, val y : Int) {
    operator fun plus(dir : Direction) : Coordinate = with(dir) {Coordinate(x = x + dx, y = y + dy)}
    operator fun plus(other : Coordinate) : Coordinate = Coordinate(x = x + other.x, y = y + other.y)
    operator fun minus(other : Coordinate) : Coordinate = Coordinate(x = x - other.x, y = y - other.y)
}

enum class Direction(val dx : Int, val dy : Int) {
    NORTH(dx = 0, dy = -1),
    EAST(dx = 1, dy = 0),
    SOUTH(dx = 0, dy = 1),
    WEST(dx = -1, dy = 0),
}