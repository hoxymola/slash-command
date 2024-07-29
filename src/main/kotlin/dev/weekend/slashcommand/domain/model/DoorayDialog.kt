package dev.weekend.slashcommand.domain.model

/**
 * @author Jaeguk Cho
 */

data class DoorayDialog(
    val callbackId: String,
    val title: String,
    val submitLabel: String = "저장",
    val elements: List<DoorayElement>,
)
