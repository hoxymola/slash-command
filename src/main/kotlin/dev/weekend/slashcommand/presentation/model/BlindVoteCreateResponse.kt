package dev.weekend.slashcommand.presentation.model

import dev.weekend.slashcommand.domain.model.DoorayAttachment

/**
 * @author Jaeguk Cho
 */

data class BlindVoteCreateResponse(
    val text: String,
    val responseType: String,
    val replaceOriginal: Boolean? = null,
    val deleteOriginal: Boolean? = null,
    val attachments: List<DoorayAttachment>? = null,
)
