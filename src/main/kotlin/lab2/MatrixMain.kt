package lab2

import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.stream.IntStream
import kotlin.system.measureTimeMillis

fun main() {
    val n = 1000
    val m = 1000
    val k = 1000
    val a = createMatrix(n, m)
    val b = createMatrix(m, k)
    val time1 = measureTimeMillis { mul(a, b) }
    val time2 = measureTimeMillis { parallelMul(a, b, 8) }
    println(time1)
    println(time2)
//    val c = mul(a, b)
//    val cParallel1 = parallelMul(a, b)
//    print(a)
//    print(b)
//    print(c)
//    print(cParallel1)
}

fun createMatrix(n: Int, m: Int, zero: Boolean = false): Array<IntArray> {
    return Array(n) {
        IntArray(m) {
            if (!zero) (0..100).random() else 0
        }
    }
}

fun mul(a: Array<IntArray>, b: Array<IntArray>): Array<IntArray> {
    val res = createMatrix(a.size, b[0].size, true)

    for (i in res.indices) {
        for (j in res[0].indices) {
            res[i][j] = mul(a, b, i, j)
        }
    }

    return res
}

fun parallelMul(a: Array<IntArray>, b: Array<IntArray>, threadNum: Int = 1): Array<IntArray> {
    val res = createMatrix(a.size, b[0].size)
    val executor = Executors.newFixedThreadPool(threadNum)

    for (i in res.indices) {
        executor.execute {
            for (j in res[0].indices) {
                res[i][j] = mul(a, b, i, j)
            }
        }
    }

    executor.shutdown()
    executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS)
    return res
}

fun mul(a: Array<IntArray>, b: Array<IntArray>, row: Int, col: Int): Int {
    return IntStream.range(0, b.size)
        .map { i -> a[row][i] * b[i][col] }
        .sum()
}

fun print(matrix: Array<IntArray>) {
    matrix.forEach {row ->
        row.forEach {
            print("$it ")
        }
        println()
    }
}