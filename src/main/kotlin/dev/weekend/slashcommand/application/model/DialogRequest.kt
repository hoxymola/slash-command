package dev.weekend.slashcommand.application.model

import dev.weekend.slashcommand.domain.model.DoorayDialog

/**
 * @author Jaeguk Cho
 */

data class DialogRequest(
    val token: String,
    val triggerId: String,
    val callbackId: String,
    val dialog: DoorayDialog,
)
