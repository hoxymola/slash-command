package dev.weekend.slashcommand.domain.extension

/**
 * @author Jaeguk Cho
 */

fun String.toMonospacedFont(): String {
    return "```\n$this\n```"
}

fun List<String>.getRandom(length: Int = 1): String {
    return this.shuffled().take(length).joinToString("")
}
