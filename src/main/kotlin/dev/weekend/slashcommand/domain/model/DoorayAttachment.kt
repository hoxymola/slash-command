package dev.weekend.slashcommand.domain.model

/**
 * @author Jaeguk Cho
 */

data class DoorayAttachment(
    val title: String? = null,
    val titleLink: String? = null,
    val fields: List<DoorayField>? = null,
    val callbackId: String? = null,
    val actions: List<DoorayAction>? = null,
    val imageUrl: String? = null,
    val text: String? = null,
    val color: String = "blue",
)
