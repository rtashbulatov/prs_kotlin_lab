package lab1

import java.io.File
import java.util.stream.Collectors
import java.util.stream.IntStream
import kotlin.streams.toList

fun main() {
    val path = getPathDependsOnOs()

    val vectors = listOf(
        generateRandomVector(30000000, -10, 10),
        generateRandomVector(30000000, -10, 10)
    )

    writeVectorsToFile(path, vectors)
}

fun generateRandomVector(size: Int, min: Int, max: Int): List<Int> {
    return IntStream.range(0, size).map { (min..max).random() }.toList()
}

fun writeVectorsToFile(path: String, vectors: List<List<Int>>) {
    val text = vectors.stream().map { vector ->
        vector.stream().map(Int::toString).collect(Collectors.joining(" "))
    }.collect(Collectors.joining("\n"))
    return File(path).writeText(text)
}

