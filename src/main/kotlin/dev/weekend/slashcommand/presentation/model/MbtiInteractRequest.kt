package dev.weekend.slashcommand.presentation.model

import dev.weekend.slashcommand.domain.enums.MbtiInteractionType
import dev.weekend.slashcommand.domain.model.DoorayUser

/**
 * @author Jaeguk Cho
 */

data class MbtiInteractRequest(
    val user: DoorayUser,
    val actionName: MbtiInteractionType,
    val actionValue: String,
    val responseUrl: String,
) {
    val userId = user.id.toLong()
    val testNo = actionValue.substringBefore(':').toLongOrNull() ?: 0
    val seq = actionValue.substringAfter(':').toIntOrNull() ?: 0
}
