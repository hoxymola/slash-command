package dev.weekend.slashcommand.presentation.model

import dev.weekend.slashcommand.application.model.LunchActionSummary
import dev.weekend.slashcommand.domain.enums.DoorayResponseType
import dev.weekend.slashcommand.domain.enums.LunchInteractionType
import dev.weekend.slashcommand.domain.enums.LunchItemType
import dev.weekend.slashcommand.domain.extension.toModelOrNull
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
) {
    val userId = user.id.toLong()
    val summary: LunchActionSummary
        get() = actionValue.takeIf { it.isNotBlank() }?.toModelOrNull<LunchActionSummary>() ?: LunchActionSummary()
}
