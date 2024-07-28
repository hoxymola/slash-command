package dev.weekend.slashcommand.domain.model

/**
 * @author Jaeguk Cho
 */

data class DoorayElement(
    val type: String = "text",
    val subType: String? = null,
    val label: String = "",
    val name: String,
    val value: String?,
    val minLength: Int = 1,
    val maxLength: Int = 150,
    val optional: Boolean = false,
)
