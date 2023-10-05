package lab3

fun main() {

}

fun createMatrix(n: Int, m: Int, zero: Boolean = false): Array<IntArray> {
    return Array(n) {
        IntArray(m) {
            if (!zero) (0..100).random() else 0
        }
    }
}
