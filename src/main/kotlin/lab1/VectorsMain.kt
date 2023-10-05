package lab1

import java.io.File
import java.util.stream.Collectors
import java.util.stream.IntStream
import kotlin.system.measureTimeMillis

fun main() {
    val path = getPathDependsOnOs()
    val rows = readFileRows(path)

    val a = mapToDoubleVector(rows[0])
    val b = mapToDoubleVector(rows[1])

    var res: Double
    val time = measureTimeMillis {
        res = dot(a, b)
    }
    println("Elapsed millis: $time")
    println("Result: $res")
}

fun readFileRows(fileName: String): List<String> {
    return File(fileName).readLines()
}

fun mapToDoubleVector(x: String): List<Double> {
    return x.split(" ").stream()
        .map(String::toDouble)
        .collect(Collectors.toList())
}

fun dot(a: List<Double>, b: List<Double>): Double {
    if (a.size != b.size) {
        throw IllegalArgumentException()
    }
    return IntStream.range(0, a.size).mapToDouble{ i -> a[i] * b[i] }.sum()
}