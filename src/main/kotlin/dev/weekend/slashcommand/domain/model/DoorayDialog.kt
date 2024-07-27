package dev.weekend.slashcommand.domain.model

/**
 * @author Jaeguk Cho
 */

data class DoorayDialog(
    val callbackId: String,
    val title: String,
    val submitLabel: String,
    val elements: List<DoorayElement>,
)
