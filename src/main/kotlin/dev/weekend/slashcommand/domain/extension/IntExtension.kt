package dev.weekend.slashcommand.domain.extension

/**
 * @author Jaeguk Cho
 */

fun Int.toPercent(total: Int): Int {
    return this * 100 / total
}

fun Int.toGraphBar(): String {
    return "█".repeat(this)
}
