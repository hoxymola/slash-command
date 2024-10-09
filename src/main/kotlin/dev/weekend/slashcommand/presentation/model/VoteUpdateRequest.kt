package dev.weekend.slashcommand.presentation.model

import dev.weekend.slashcommand.domain.enums.VoteInteractionType
import dev.weekend.slashcommand.domain.enums.VoteInteractionType.*
import dev.weekend.slashcommand.domain.model.DoorayChannel
import dev.weekend.slashcommand.domain.model.DoorayTenant
import dev.weekend.slashcommand.domain.model.DoorayUser
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException

/**
 * @author Jaeguk Cho
 */

data class VoteUpdateRequest(
    val user: DoorayUser,
    val actionName: VoteInteractionType?, // 대화상자 응답 시에는 null
    val actionValue: String?,
    val callbackId: String,
    val tenant: DoorayTenant,
    val channel: DoorayChannel,
    val cmdToken: String,
    val triggerId: String,
    val responseUrl: String,
    val submission: Map<VoteInteractionType, String> = emptyMap(), // 대화상자 응답 시에만 오는 값
) {
    val voteNo = callbackId.substringBefore(':').toLong()
    val voteItemNo = callbackId.substringAfter(':').toLongOrNull()
    val voteTitle = submission[CHANGE_TITLE]?.trim() ?: throw NotFoundException()
    val voteLink = submission[LINK]
    val voteItem = (submission[ADD_ITEM] ?: submission[CHANGE_ITEM])?.trim() ?: throw NotFoundException()
}
