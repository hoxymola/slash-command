package dev.weekend.slashcommand.domain.model

/**
 * @author Jaeguk Cho
 */

data class DoorayElement(
    val type: String = "text",
    val label: String = "",
    val name: String,
    val minLength: Int = 1,
    val maxLength: Int = 100,
)
