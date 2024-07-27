package dev.weekend.slashcommand.domain.model

/**
 * @author Jaeguk Cho
 */

data class DoorayAction(
    val name: String,
    val type: String,
    val text: String,
    val value: String,
    val style: String? = null,
)
