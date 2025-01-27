package dev.weekend.slashcommand.presentation.model

import dev.weekend.slashcommand.domain.enums.LunchInteractionType
import dev.weekend.slashcommand.domain.model.DoorayOriginalMessage
import dev.weekend.slashcommand.domain.model.DoorayTenant
import dev.weekend.slashcommand.domain.model.DoorayUser

/**
 * @author Yoohwa Cho
 */
data class LunchInteractRequest(
    val user: DoorayUser,
    val actionName: LunchInteractionType,
    val actionValue: String,
    val tenant: DoorayTenant,
    val originalMessage: DoorayOriginalMessage,
) {
    val userId = user.id.toLong()
}
