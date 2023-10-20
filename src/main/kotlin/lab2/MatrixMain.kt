package lab2

import java.util.Random
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
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
    var res3: Array<IntArray>
    val time1 = measureTimeMillis { res1 = mul(a, b) }
    val time2 = measureTimeMillis { res2 = parallelMul(a, b, 4) }
    val time3 = measureTimeMillis { res3 = parallelMul2(a, b, 4) }
    assert(equals(res1, res2))
    assert(equals(res2, res3))
    println(time1)
    println(time2)
    println(time3)
}

fun createMatrix(n: Int, m: Int, random: Random? = null): Array<IntArray> {
    return Array(n) {
        IntArray(m) {
            random?.nextInt() ?: 0
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

fun parallelMul2(a: Array<IntArray>, b: Array<IntArray>, threadNum: Int = 1): Array<IntArray> {
    val res = createMatrix(a.size, b[0].size)
    val executor = Executors.newFixedThreadPool(threadNum)

    for (i in 0..<threadNum) {
        executor.execute {
            blockMul(a, b, res, threadNum, i)
        }
    }

    executor.shutdown()
    executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS)
    return res
}

fun blockMul(a: Array<IntArray>, b: Array<IntArray>, res: Array<IntArray>, threadNum: Int, threadIdx: Int) {
    val blockSize = res.size * res[0].size / threadNum
    val handledSize = threadIdx * blockSize
    val realBlockSize = (res.size * res[0].size - handledSize).coerceAtMost(blockSize)
    for (k in handledSize..<handledSize + realBlockSize) {
        val i = k / res[0].size
        val j = k % res[0].size
        res[i][j] = mul(a, b, i, j)
    }
}

fun mul(a: Array<IntArray>, b: Array<IntArray>, row: Int, col: Int): Int {
    return IntStream.range(0, b.size)
        .map { i -> a[row][i] * b[i][col] }
        .sum()
}

fun equals(a: Array<IntArray>, b: Array<IntArray>): Boolean {
    return a.contentDeepEquals(b)
}