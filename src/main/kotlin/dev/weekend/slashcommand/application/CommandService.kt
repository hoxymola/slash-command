package dev.weekend.slashcommand.application

import dev.weekend.slashcommand.application.model.DialogRequest
import dev.weekend.slashcommand.domain.entity.BlindVote
import dev.weekend.slashcommand.domain.entity.BlindVoteItem
import dev.weekend.slashcommand.domain.enums.VoteInteractionType.*
import dev.weekend.slashcommand.domain.extension.toJson
import dev.weekend.slashcommand.domain.model.DoorayDialog
import dev.weekend.slashcommand.domain.model.DoorayElement
import dev.weekend.slashcommand.domain.repository.BlindVoteItemRepository
import dev.weekend.slashcommand.domain.repository.BlindVoteRepository
import dev.weekend.slashcommand.infrastructure.client.DoorayClient
import dev.weekend.slashcommand.presentation.model.CommandResponse
import dev.weekend.slashcommand.presentation.model.FormCreateRequest
import dev.weekend.slashcommand.presentation.model.VoteUpdateRequest
import kotlinx.coroutines.runBlocking
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

/**
 * @author Jaeguk Cho
 */

@Service
class CommandService(
    private val blindVoteRepository: BlindVoteRepository,
    private val blindVoteItemRepository: BlindVoteItemRepository,
    private val doorayClient: DoorayClient,
) {
    fun createBlindVote(
        createRequest: FormCreateRequest,
    ): CommandResponse {
        val vote = BlindVote.createBy(
            voteTitle = "무기명 투표!",
            selectableItemCnt = 3,
            userId = createRequest.userId,
        ).let { blindVoteRepository.save(it) }

        val voteItems = listOf(
            BlindVoteItem.createBy("짜장면", vote)
                .apply { increaseCnt(); increaseCnt(); increaseCnt() },
            BlindVoteItem.createBy("짬뽕", vote)
                .apply { increaseCnt(); },
            BlindVoteItem.createBy("탕수육", vote)
                .apply { increaseCnt(); increaseCnt() },
        ).let { blindVoteItemRepository.saveAll(it) }


        return CommandResponse.createFormBy(
            vote = vote,
            voteItems = voteItems,
        )
    }

    fun updateBlindVote(
        request: VoteUpdateRequest,
    ): CommandResponse {
        return when (request.actionName) {
            CHANGE_TITLE -> request.changeTitle() // 기존폼 + 제목 수정
            ADD_ITEM -> request.addItem() // 기존폼 + 항목 추가
            CHANGE_ITEM -> request.changeItem() // 기존폼 + 항목 수정
            CHANGE_SELECTABLE_ITEM_COUNT -> request.changeSelectableItemCount() // 기존폼 + 선택가능한 개수 수정
            START_VOTE -> request.startVote()
            CANCEL_VOTE -> CommandResponse.createCancelVote()
            VOTE -> request.vote()
            END_VOTE -> request.endVote()
        }
    }

    private fun VoteUpdateRequest.changeTitle(): CommandResponse {
        runBlocking {
            doorayClient.openDialog(
                tenantDomain = tenant.domain,
                channelId = channel.id,
                cmdToken = cmdToken,
                request = DialogRequest(
                    token = cmdToken,
                    triggerId = triggerId,
                    callbackId = "test-callback-id-request",
                    dialog = DoorayDialog(
                        callbackId = "test-callback-id-dialog",
                        title = "test-title",
                        submitLabel = "test-submit-label",
                        elements = listOf(
                            DoorayElement(
                                label = "test-label",
                                name = "test-name",
                                placeholder = "test-placeholder",
                            ),
                        ),
                    ),
                ),
            )
        }

        return CommandResponse.createTempResponse(CHANGE_TITLE, this.toJson())
    }

    private fun VoteUpdateRequest.addItem(): CommandResponse {
        return CommandResponse.createTempResponse(ADD_ITEM, this.toJson())
    }

    private fun VoteUpdateRequest.changeItem(): CommandResponse {
        return CommandResponse.createTempResponse(CHANGE_ITEM, this.toJson())
    }

    private fun VoteUpdateRequest.changeSelectableItemCount(): CommandResponse {
        val vote = blindVoteRepository.findByIdOrNull(voteNo) ?: throw NotFoundException()
        val voteItems = blindVoteItemRepository.findByVoteVoteNo(vote.voteNo)
        val selectableItemCount = actionValue.toInt()
        vote.updateSelectableItemCnt(selectableItemCount)

        return CommandResponse.createFormBy(
            vote = vote,
            voteItems = voteItems,
        )
    }

    private fun VoteUpdateRequest.startVote(): CommandResponse {
        val vote = blindVoteRepository.findByIdOrNull(voteNo) ?: throw NotFoundException()
        val voteItems = blindVoteItemRepository.findByVoteVoteNo(vote.voteNo)

        return CommandResponse.createVoteBy(
            vote = vote,
            voteItems = voteItems,
            deleteOriginal = true,
        )
    }

    private fun VoteUpdateRequest.vote(): CommandResponse {
        return CommandResponse.createTempResponse(VOTE, this.toJson())
    }

    private fun VoteUpdateRequest.endVote(): CommandResponse {
        return CommandResponse.createTempResponse(END_VOTE, this.toJson())
    }
}
