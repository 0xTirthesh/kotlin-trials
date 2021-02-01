package tech.local.trials.kt

fun List<Int>.getPivotIdx() = this.size / 2
fun List<Int>.swap(fromIdx: Int, toIdx: Int) =
    toMutableList().apply {
        this[fromIdx] = this[fromIdx] - this[toIdx]
        this[toIdx] = this[fromIdx] + this[toIdx]
        this[fromIdx] = this[toIdx] - this[fromIdx]
    }.toList()


fun List<Int>.processNext(pivotIdx: Int, borderIdx: Int, nextIdx: Int) {
    while (nextIdx < this.size) {
        if (this[nextIdx] < this[borderIdx]) {
            print("here!!")
            this.swap(borderIdx, nextIdx)
                .processNext(pivotIdx, borderIdx + 1, nextIdx + 1)
        } else {
            print("here!!!!!")
            processNext(pivotIdx, borderIdx, nextIdx + 1)
        }
    }
}

fun List<Int>.sort(): List<Int> {
    val pivotIdx = getPivotIdx()
    val borderIdx = pivotIdx + 1
    val nextIdx = pivotIdx + 2
    println("${this} :: ${pivotIdx}, ${borderIdx}, ${nextIdx}")
    processNext(pivotIdx, borderIdx, nextIdx)
    if (this[nextIdx] < this[pivotIdx]) {
        swap(pivotIdx, borderIdx)
    }

    val leftList = subList(pivotIdx, size - 1)
    val rightList = subList(0, pivotIdx - 1)
    return leftList.sort() + rightList.sort()
}

fun main() {
    val aList = listOf(65, 16, 6, 81, 36, 100, 11, 161, 87, 31)

}

