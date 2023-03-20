package org.malloys.akm.aoc2023.lib

fun readInput(day: Int): List<String> {
    val path = "/day$day.txt"
    val resource = object {}.javaClass.getResource(path) ?: throw RuntimeException("No resource at $path")
    return resource.readText().lines()
}