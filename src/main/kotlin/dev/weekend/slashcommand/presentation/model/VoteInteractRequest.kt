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

data class VoteInteractRequest(
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
    val userId = user.id.toLong()
    val voteNo = callbackId.substringBefore(':').toLong()
    val voteItemNo = callbackId.substringAfter(':').toLongOrNull()

    val voteTitle
        get() = submission[CHANGE_TITLE]?.trim() ?: throw NotFoundException()
    val voteLink
        get() = submission[LINK]
    val voteItem
        get() = (submission[ADD_ITEM] ?: submission[CHANGE_ITEM])?.trim() ?: throw NotFoundException()
}
