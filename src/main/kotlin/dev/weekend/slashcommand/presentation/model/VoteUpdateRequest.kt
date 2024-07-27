package dev.weekend.slashcommand.presentation.model

import dev.weekend.slashcommand.domain.enums.VoteInteractionType
import dev.weekend.slashcommand.domain.model.DoorayChannel
import dev.weekend.slashcommand.domain.model.DoorayTenant
import dev.weekend.slashcommand.domain.model.DoorayUser

/**
 * @author Jaeguk Cho
 */

data class VoteUpdateRequest(
    val user: DoorayUser,
    val actionName: VoteInteractionType,
    val actionValue: String,
    val callbackId: String,
    val tenant: DoorayTenant,
    val channel: DoorayChannel,
    val commandName: String,
    val command: String,
    val text: String,
    val appToken: String,
    val cmdToken: String,
    val triggerId: String,
    val commandRequestUrl: String,
    val channelLogId: String,
    val originalMessage: CommandResponse,
    val responseUrl: String,
) {
    val voteNo = callbackId.toLong()
}
