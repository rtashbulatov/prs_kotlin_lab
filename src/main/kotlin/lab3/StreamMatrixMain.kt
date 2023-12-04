package lab3

import java.util.*
import java.util.stream.IntStream
import kotlin.system.measureTimeMillis

fun main() {
    val rnd = Random(123)
    val n = 1000
    val m = 1000
    val k = 1000

    val a = createMatrix(n, m, rnd)
    val b = createMatrix(m, k, rnd)
    var res1: Array<IntArray>
    var res2: Array<IntArray>
    val time1 = measureTimeMillis { res1 = mul(a, b) }
    val time2 = measureTimeMillis { res2 = parallelStreamMul(a, b) }

    assert(equals(res1, res2))
    println(time1)
    println(time2)
//    print(res1)
//    print(res2)
}

fun createMatrix(n: Int, m: Int, random: Random? = null): Array<IntArray> {
    return Array(n) {
        IntArray(m) {
            random?.nextInt(4) ?: 0
        }
    }
}

fun mul(a: Array<IntArray>, b: Array<IntArray>): Array<IntArray> {
    val res = createMatrix(a.size, b[0].size)

    for (i in res.indices) {
        for (j in res[0].indices) {
            res[i][j] = mul(a, b, i, j)
        }
    }

    return res
}

fun parallelStreamMul(a: Array<IntArray>, b: Array<IntArray>): Array<IntArray> {
    val res = createMatrix(a.size, b[0].size)

    IntStream.range(0, res.size * res[0].size).parallel().forEach {
        val i = it / res[0].size
        val j = it % res[0].size
        res[i][j] = mul(a, b, i, j)
    }

    return res
}

fun mul(a: Array<IntArray>, b: Array<IntArray>, row: Int, col: Int): Int {
    return IntStream.range(0, b.size)
        .map { i -> a[row][i] * b[i][col] }
        .sum()
}

fun parallelMul(a: Array<IntArray>, b: Array<IntArray>, row: Int, col: Int): Int {
    return IntStream.range(0, b.size)
        .parallel()
        .map { i -> a[row][i] * b[i][col] }
        .sum()
}

fun equals(a: Array<IntArray>, b: Array<IntArray>): Boolean {
    return a.contentDeepEquals(b)
}

fun print(a: Array<IntArray>) {
    a.forEach { row ->
        row.forEach {
            print("$it ")
        }
        println()
    }
}