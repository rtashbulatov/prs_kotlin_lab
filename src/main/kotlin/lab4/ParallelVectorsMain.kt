package lab4

import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.Semaphore
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger
import java.util.stream.IntStream
import kotlin.streams.toList
import kotlin.system.measureTimeMillis

val rnd = Random(123)

fun main() {
    val size = 500000000
    val a = generateRandomVector(size)
    val b = generateRandomVector(size)

    val threads = 24
    val res0: Int
    val time0 = measureTimeMillis { res0 = dot(a, b) }
    println("A ($time0 ms): $res0")

    val res1: Int
    val time1 = measureTimeMillis { res1 = parallelDot(a, b, threads) }
    val res4: Int
    val time4 = measureTimeMillis { res4 = atomicIntegerParallelDot(a, b, threads) }
    val res2: Int
    val time2 = measureTimeMillis { res2 = synchronizedParallelDot(a, b, threads) }
    val res3: Int
    val time3 = measureTimeMillis { res3 = semaphoreParallelDot(a, b, threads) }
    println("B ($time1 ms): $res1")
    println("C ($time2 ms): $res2")
    println("D ($time3 ms): $res3")
    println("E ($time4 ms): $res4")
}

fun parallelDot(a: List<Int>, b: List<Int>, threads: Int): Int {
    val executor = Executors.newFixedThreadPool(threads)
    val n = (a.size + threads - 1) / threads

    var res = 0
    for (i in 0..<threads) {
        executor.execute {
            res += dot(a, b, n * i, n * (i + 1))
        }
    }

    executor.shutdown()
    executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS)
    return res
}

fun synchronizedParallelDot(a: List<Int>, b: List<Int>, threads: Int): Int {
    val lock = Any()
    val executor = Executors.newFixedThreadPool(threads)
    val n = (a.size + threads - 1) / threads

    var res = 0
    for (i in 0..<threads) {
        executor.execute {
            val localRes = dot(a, b, n * i, n * (i + 1))
            synchronized(lock) {
                res += localRes
            }
        }
    }

    executor.shutdown()
    executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS)
    return res
}

fun semaphoreParallelDot(a: List<Int>, b: List<Int>, threads: Int): Int {
    val semaphore = Semaphore(1)
    val executor = Executors.newFixedThreadPool(threads)
    val n = (a.size + threads - 1) / threads

    var res = 0
    for (i in 0..<threads) {
        executor.execute {
            val localRes = dot(a, b, n * i, n * (i + 1))
            semaphore.acquire()
            res += localRes
            semaphore.release()
        }
    }

    executor.shutdown()
    executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS)
    return res
}

fun atomicIntegerParallelDot(a: List<Int>, b: List<Int>, threads: Int): Int {
    val executor = Executors.newFixedThreadPool(threads)
    val n = (a.size + threads - 1) / threads

    val res = AtomicInteger(0)
    for (i in 0..<threads) {
        executor.execute {
            res.addAndGet(dot(a, b, n * i, n * (i + 1)))
        }
    }

    executor.shutdown()
    executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS)
    return res.get()
}

fun generateRandomVector(size: Int): List<Int> {
    return IntStream.range(0, size)
        .map { rnd.nextInt(4) }
        .toList()
}

fun dot(a: List<Int>, b: List<Int>): Int {
    return dot(a, b, 0, a.size)
}

fun dot(a: List<Int>, b: List<Int>, l: Int, r: Int): Int {
    require(a.size == b.size)
    return IntStream.range(l, r.coerceAtMost(a.size)).map {
        i -> a[i] * b[i]
    }.sum()
}