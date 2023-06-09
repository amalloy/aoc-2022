package org.malloys.akm.aoc2022.lib

fun readInput(day : Int) : List<String> {
    val path = "/day$day.txt"
    val resource = object {}.javaClass.getResource(path) ?: throw RuntimeException("No resource at $path")
    return resource.readText().lines()
}

fun List<String>.splitAtBlankLines() : List<List<String>> {
    val iter = iterator()
    fun chunk() : List<String> = generateSequence {
        val line = if (iter.hasNext()) {
            iter.next()
        } else {
            ""
        }
        line.ifEmpty {
            null
        }
    }.toList()
    return buildList {
        while (iter.hasNext()) {
            add(chunk())
        }
    }
}