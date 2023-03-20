package org.malloys.akm.aoc2023.lib

fun readInput(day: Int): List<String> {
    val path = "/day$day.txt"
    val resource = object {}.javaClass.getResource(path) ?: throw RuntimeException("No resource at $path")
    return resource.readText().lines()
}

fun List<String>.groupsAcrossBlankLines(): List<List<String>> {
    val iter = iterator()
    return buildList {
        fun chunk(): List<String> = generateSequence {
            val line: String = if (iter.hasNext()) {
                iter.next()
            } else {
                ""
            }
            if (line.isEmpty()) {
                return@generateSequence null
            }
            line
        }.toList()
        while (iter.hasNext()) {
            add(chunk())
        }
    }
}