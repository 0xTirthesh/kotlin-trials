package tech.local.trials.kt

fun List<Int>.getPivotIdx(lowIdx: Int, highIdx: Int): Int {
    val midIdx = (lowIdx + highIdx) / 2
    if (get(lowIdx) < get(midIdx)) {
        if (get(midIdx) < get(highIdx)) {

            return midIdx
        }
    } else if (get(lowIdx) < get(highIdx)) {

        return lowIdx
    }


    return highIdx

}

fun List<Int>.swapValues(fromIdx: Int, toIdx: Int) =
    toMutableList().apply {
        val tmpValue = this[fromIdx]
        this[fromIdx] = this[toIdx]
        this[toIdx] = tmpValue
    }.toList()

fun List<Int>.performSort(lowIdx: Int, highIdx: Int): Pair<Int, List<Int>> {
    val pivotIdx = getPivotIdx(lowIdx, highIdx)
    val pivotValue = get(pivotIdx)

    println("${this} :: ${pivotValue}")

    val (borderIdx, someList) =
        lowIdx.rangeTo(highIdx).fold(lowIdx to swapValues(pivotIdx, lowIdx)) { (borderIdx, lst), idx ->
            if (get(idx) < pivotValue) {
                borderIdx + 1 to lst.swapValues(borderIdx, idx)
            } else {
                borderIdx to lst
            }
        }

    return borderIdx to someList.swap(lowIdx, borderIdx)
}

fun List<Int>.quickSort(lowIdx: Int, highIdx: Int): List<Int> =
    if (lowIdx < highIdx) {
        val (pivotIdx, newList) =
            performSort(lowIdx, highIdx)


        val final = newList.quickSort(lowIdx, pivotIdx - 1)
            .quickSort(pivotIdx + 1, highIdx)

        println("${final}")
        final
    } else {
        this
    }


fun List<Int>.performSimpleQuickSort(): List<Int> =
    if (size > 1) {
        get(size / 2).let { pivotValue ->
            val smaller = filter { it < pivotValue }.performSimpleQuickSort()
            val equals = filter { it == pivotValue }
            val larger = filter { it > pivotValue }.performSimpleQuickSort()

            (smaller + equals + larger) //.apply { println(this) }
        }
    } else {
        this
    }


fun main() {
    val aList = listOf(65, 16, 6, 81, 36, 100, 11, 161, 87, 31)
    println(aList.sorted() == aList.performSimpleQuickSort())

    val bList = listOf(100, 200, 300)
    println(bList.sorted() == bList.performSimpleQuickSort())

    val cList = listOf(100, 99, 98)
    println(cList.sorted() == cList.performSimpleQuickSort())

    val dList = listOf(1)
    println(dList.sorted() == dList.performSimpleQuickSort())

    val eList = listOf(64, 16, 51, 616, 16, 168, 165, 61, 65, 16, 1, 31, 979, 651, 651, 41, 684)
    println(eList.sorted() == eList.performSimpleQuickSort())

}

