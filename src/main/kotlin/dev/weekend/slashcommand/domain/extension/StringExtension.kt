package dev.weekend.slashcommand.domain.extension

/**
 * @author Jaeguk Cho
 */

fun String.toMonospacedFont(): String {
    return "```\n$this\n```"
}
