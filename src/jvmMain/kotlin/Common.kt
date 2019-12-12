import java.io.File

fun readLines(path: String): List<String> {
    val file = openFile(path)
    return file.lines()
}

fun openFile(path: String): String {
    val resourcePath = object {}
        .javaClass
        .getResource(path)
        .toURI()

    return File(resourcePath).readText().trimEnd()
}