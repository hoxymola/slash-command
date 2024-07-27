package dev.weekend.slashcommand.domain.model

/**
 * @author Jaeguk Cho
 */

data class DoorayElement(
    val type: String = "textarea",
    val label: String,
    val name: String,
    val minLength: Int = 1,
    val maxLength: Int = 100,
    val placeholder: String,
)
