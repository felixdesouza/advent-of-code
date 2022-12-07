package aoc2022

import common.Problem

object Day7 : Problem() {

    val input = rawInput.let { parse(it) }
    val testInput = """
        ${'$'} cd /
        ${'$'} ls
        dir a
        14848514 b.txt
        8504156 c.dat
        dir d
        ${'$'} cd a
        ${'$'} ls
        dir e
        29116 f
        2557 g
        62596 h.lst
        ${'$'} cd e
        ${'$'} ls
        584 i
        ${'$'} cd ..
        ${'$'} cd ..
        ${'$'} cd d
        ${'$'} ls
        4060174 j
        8033020 d.log
        5626152 d.ext
        7214296 k
    """.trimIndent().let { parse(it) }

    private fun parse(input: String): List<Command> {
        val lines = input.lines()
        val cdRegex = "cd ([a-zA-Z0-9./]+)".toRegex()

        return lines.plus("$ ")
                .withIndex()
                .filter { (_, output) -> output.startsWith("$ ") }
                .map { it.copy(value = it.value.substringAfter("$ ")) }
                .windowed(2)
                .map { (current, next) ->
                    when {
                        cdRegex.matches(current.value) -> ChangeDirectory(cdRegex.matchEntire(current.value)!!.groups[1]!!.value)
                        current.value == "ls" -> {
                            ListDirectory(lines.subList(current.index + 1, next.index))
                        }

                        else -> throw AssertionError("not processed: $current")
                    }
                }
    }

    fun part1(input: List<Command>): Long {
        val allFiles = walk(input)
        val flatDirectories = allFiles.groupBy { it.name().dropLast(1) }
        return flatDirectories.map { size(flatDirectories, it.key) }.filter { it <= 100000 }.sum()
    }

    private fun walk(input: List<Command>): List<FileTreeElement> {
        val (_, allFiles) = input.fold(listOf<String>() to listOf<FileTreeElement>()) { state, command ->
            val (currentDirectory, files) = state
            when (command) {
                is ChangeDirectory -> {
                    if (command.directory == "..") {
                        currentDirectory.dropLast(1) to files
                    } else {
                        currentDirectory.plus(command.directory) to files
                    }
                }

                is ListDirectory -> {
                    val newFiles = command.contents
                            .map {
                                when {
                                    it.startsWith("dir ") ->
                                        Directory(currentDirectory.plus(it.substringAfter("dir ")))

                                    else -> {
                                        val (size, filename) = it.split(" ")
                                        File(currentDirectory.plus(filename), size.toLong())
                                    }
                                }
                            }
                    currentDirectory to files.plus(newFiles)
                }

                else -> throw AssertionError("bla")
            }
        }
        return allFiles
    }

    private fun size(directories: Map<List<String>, List<FileTreeElement>>, directory: List<String>): Long {
        val contents = directories[directory]
        return contents!!
                .map {
                    when (it) {
                        is File -> it.size
                        is Directory -> size(directories, it.name)
                        else -> throw AssertionError("bla")
                    }
                }
                .sum()
    }

    fun part2(input: List<Command>): Long {
        val allFiles = walk(input)
        val flatDirectories = allFiles.groupBy { it.name().dropLast(1) }
        val unusedSpace = 70_000_000 - size(flatDirectories, listOf("/"))
        val requiredSpace = 30_000_000 - unusedSpace
        return flatDirectories.map { size(flatDirectories, it.key) }.filter { it >= requiredSpace }.min()!!
    }

    interface Command
    data class ListDirectory(val contents: List<String>) : Command
    data class ChangeDirectory(val directory: String) : Command

    interface FileTreeElement {
        fun name(): List<String>
    }

    data class Directory(val name: List<String>) : FileTreeElement {
        override fun name(): List<String> = name
    }

    data class File(val name: List<String>, val size: Long) : FileTreeElement {
        override fun name(): List<String> {
            return name
        }
    }

    @JvmStatic
    fun main(args: Array<String>) {
        println(part1(input))
        println(part2(input))
    }
}

