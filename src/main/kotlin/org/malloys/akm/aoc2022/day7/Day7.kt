package org.malloys.akm.aoc2022.day7

import org.malloys.akm.aoc2022.lib.readInput

sealed interface TranscriptEntry
object Ls : TranscriptEntry
data class Cd(val dir : String) : TranscriptEntry
data class FileListing(val size : Int, val name : String) : TranscriptEntry
object DirListing : TranscriptEntry

sealed class FilesystemObject(val name : String, val parent : DirectoryObject?) {
    abstract val totalSize : Int
}

class FileObject(name : String, parent : DirectoryObject?, size : Int) : FilesystemObject(name, parent) {
    override val totalSize : Int = size
}

class DirectoryObject(name : String, parent : DirectoryObject?) : FilesystemObject(name, parent) {
    val children = mutableMapOf<String, FilesystemObject>()
    override val totalSize : Int by lazy {
        children.values.sumOf(FilesystemObject::totalSize)
    }

    fun descendants() : Sequence<DirectoryObject> {
        return sequence {
            yield(this@DirectoryObject)
            yieldAll(children.values.asSequence().flatMap {
                when (it) {
                    is DirectoryObject -> it.descendants()
                    is FileObject -> emptySequence()
                }
            })
        }
    }
}

private val CD = Regex("""\$ cd (/|[\w.]+)""")
private val LS = Regex("""\$ ls""")
private val DIR = Regex("""dir ([\w+.]+)""")
private val FILE = Regex("""(\d+) ([\w.]+)""")
fun parse(entry : String) : TranscriptEntry {
    return CD.matchEntire(entry)?.let {Cd(it.groups[1]!!.value)}
        ?: LS.matchEntire(entry)?.let {Ls}
        ?: DIR.matchEntire(entry)?.let {DirListing}
        ?: FILE.matchEntire(entry)?.let {FileListing(it.groups[1]!!.value.toInt(), it.groups[2]!!.value)}
        ?: throw RuntimeException("Can't parse $entry")
}

fun buildTree(transcript : List<TranscriptEntry>) : DirectoryObject {
    if (transcript[0] != Cd("/")) {
        throw RuntimeException("Expected to start at root, not ${transcript[0]}")
    }
    var cwd = DirectoryObject("/", null)
    for (entry in transcript.subList(1, transcript.size)) {
        when (entry) {
            is Cd ->
                cwd = when (entry.dir) {
                    ".." -> cwd.parent ?: throw RuntimeException("No parent for ${cwd.name}")
                    else -> (cwd.children[entry.dir] as? DirectoryObject) ?: run {
                        DirectoryObject(entry.dir, cwd).also {
                            cwd.children[entry.dir] = it
                        }
                    }
                }

            is FileListing -> cwd.children[entry.name] = FileObject(entry.name, cwd, entry.size)
            is DirListing -> {}
            is Ls -> {}
        }
    }

    while (cwd.parent != null) {
        cwd = cwd.parent!!
    }
    return cwd
}

fun main() {
    val tree = readInput(7).map {parse(it)}.let {buildTree(it)}
    println("Booted up. ${tree.totalSize} bytes accounted for.")
    println("Part 1: ${part1(tree)}")
    println("Part 2: ${part2(tree)}")
}

fun part1(tree : DirectoryObject) : Int {
    return tree.descendants().filter {it.totalSize <= 100000}.sumOf {it.totalSize}
}

const val TOTAL_SPACE = 70000000
const val SPACE_NEEDED = 30000000

fun part2(tree : DirectoryObject) : Int {
    val spaceFree = TOTAL_SPACE - tree.totalSize
    val deletionSizeNeeded = SPACE_NEEDED - spaceFree
    println("$spaceFree free, $deletionSizeNeeded to be deleted")
    return tree.descendants().filter {it.totalSize >= deletionSizeNeeded}.minOf {it.totalSize}
}