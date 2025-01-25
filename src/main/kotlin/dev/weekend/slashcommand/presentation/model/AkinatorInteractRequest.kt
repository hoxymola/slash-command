package dev.weekend.slashcommand.presentation.model

import dev.weekend.slashcommand.domain.enums.AkinatorAnswerType
import dev.weekend.slashcommand.domain.enums.AkinatorInteractionType
import dev.weekend.slashcommand.domain.model.DoorayTenant
import dev.weekend.slashcommand.domain.model.DoorayUser

/**
 * @author Jaeguk Cho
 */

data class AkinatorInteractRequest(
    val user: DoorayUser,
    val actionName: AkinatorInteractionType,
    val actionValue: String,
    val tenant: DoorayTenant,
) {
    val answer = AkinatorAnswerType.getByName(actionValue)?.name.orEmpty()
}
