package dev.weekend.slashcommand.application

import dev.weekend.slashcommand.application.model.DialogRequest
import dev.weekend.slashcommand.domain.entity.BlindVote
import dev.weekend.slashcommand.domain.entity.BlindVoteItem
import dev.weekend.slashcommand.domain.entity.BlindVoteMember
import dev.weekend.slashcommand.domain.enums.DoorayResponseType.EPHEMERAL
import dev.weekend.slashcommand.domain.enums.VoteInteractionType
import dev.weekend.slashcommand.domain.enums.VoteInteractionType.*
import dev.weekend.slashcommand.domain.model.DoorayDialog
import dev.weekend.slashcommand.domain.model.DoorayElement
import dev.weekend.slashcommand.domain.repository.BlindVoteItemRepository
import dev.weekend.slashcommand.domain.repository.BlindVoteMemberRepository
import dev.weekend.slashcommand.domain.repository.BlindVoteRepository
import dev.weekend.slashcommand.infrastructure.client.DoorayClient
import dev.weekend.slashcommand.presentation.model.CommandResponse
import dev.weekend.slashcommand.presentation.model.FormCreateRequest
import dev.weekend.slashcommand.presentation.model.VoteUpdateRequest
import kotlinx.coroutines.runBlocking
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.support.TransactionTemplate

/**
 * @author Jaeguk Cho
 */

@Service
class CommandService(
    private val blindVoteRepository: BlindVoteRepository,
    private val blindVoteItemRepository: BlindVoteItemRepository,
    private val blindVoteMemberRepository: BlindVoteMemberRepository,
    private val doorayClient: DoorayClient,
    private val transactionTemplate: TransactionTemplate,
) {
    fun createBlindVote(
        createRequest: FormCreateRequest,
    ): CommandResponse {
        val vote = BlindVote.createBy(
            voteTitle = "무기명 투표!",
            selectableItemCnt = 3,
            userId = createRequest.userId,
            tenantId = createRequest.tenantId,
        ).let { blindVoteRepository.save(it) }

        val voteItems = listOf(
            BlindVoteItem.createBy("짜장면", vote),
            BlindVoteItem.createBy("짬뽕", vote),
            BlindVoteItem.createBy("탕수육", vote),
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
            CHECK_VOTE -> request.checkVote()
            VOTE -> request.vote()
            END_VOTE -> request.endVote()
        }
    }

    private fun VoteUpdateRequest.changeTitle(): CommandResponse {
        openDialog(
            title = "제목 수정",
            submitLabel = "저장",
            type = CHANGE_TITLE,
        )

        return CommandResponse.createResponse()
    }

    private fun VoteUpdateRequest.addItem(): CommandResponse {
        openDialog(
            title = "항목 추가",
            submitLabel = "저장",
            type = ADD_ITEM,
        )

        return CommandResponse.createResponse()
    }

    private fun VoteUpdateRequest.changeItem(): CommandResponse {
        openDialog(
            title = "항목 수정",
            submitLabel = "저장",
            type = CHANGE_ITEM,
        )

        return CommandResponse.createResponse()
    }

    private fun VoteUpdateRequest.changeSelectableItemCount(): CommandResponse {
        return transactionTemplate.execute {
            val vote = blindVoteRepository.findByIdOrNull(voteNo) ?: throw NotFoundException()
            val voteItems = blindVoteItemRepository.findByVoteVoteNo(vote.voteNo)
            val selectableItemCount = actionValue.toInt()
            vote.updateSelectableItemCnt(selectableItemCount)

            CommandResponse.createFormBy(
                vote = vote,
                voteItems = voteItems,
            )
        } ?: throw IllegalStateException()
    }

    private fun VoteUpdateRequest.startVote(): CommandResponse {
        val vote = blindVoteRepository.findByIdOrNull(voteNo) ?: throw NotFoundException()
        val voteItems = blindVoteItemRepository.findByVoteVoteNo(vote.voteNo)
        val voteMembers = blindVoteMemberRepository.findByVoteVoteNo(vote.voteNo)

        return CommandResponse.createVoteBy(
            vote = vote,
            voteItems = voteItems,
            voteMembers = voteMembers,
            deleteOriginal = true,
            type = START_VOTE,
            userId = user.id.toLong(),
        )
    }

    private fun VoteUpdateRequest.checkVote(): CommandResponse {
        val vote = blindVoteRepository.findByIdOrNull(voteNo) ?: throw NotFoundException()
        val voteItems = blindVoteItemRepository.findByVoteVoteNo(vote.voteNo)
        val voteMembers = blindVoteMemberRepository.findByVoteVoteNo(vote.voteNo)

        return CommandResponse.createVoteBy(
            vote = vote,
            voteItems = voteItems,
            voteMembers = voteMembers,
            responseType = EPHEMERAL,
            replaceOriginal = true,
            type = CHECK_VOTE,
            userId = user.id.toLong(),
        )
    }

    private fun VoteUpdateRequest.vote(): CommandResponse {
        return transactionTemplate.execute {
            val vote = blindVoteRepository.findByIdOrNull(voteNo) ?: throw NotFoundException()
            val voteItems = blindVoteItemRepository.findByVoteVoteNo(vote.voteNo)
            val voteMembers = blindVoteMemberRepository.findByVoteVoteNo(vote.voteNo).toMutableList()

            val targetItem = voteItems.firstOrNull { it.voteItemNo == actionValue.toLong() }
            val targetMember = voteMembers.firstOrNull {
                it.userId == user.id.toLong() && it.voteItem.voteItemNo == targetItem?.voteItemNo
            }

            if (targetMember == null) {
                targetItem ?: throw NotFoundException()

                val selectedItemCount = voteMembers.count { it.userId == user.id.toLong() }

                if (vote.selectableItemCnt > selectedItemCount) {
                    targetItem.increaseCnt()
                    BlindVoteMember.createBy(
                        vote = vote,
                        voteItem = targetItem,
                        userId = user.id.toLong(),
                    ).let { blindVoteMemberRepository.save(it) }
                        .also { voteMembers.add(it) }
                }
            } else {
                targetItem?.decreaseCnt()
                blindVoteMemberRepository.delete(targetMember)
                voteMembers.remove(targetMember)
            }

            CommandResponse.createVoteBy(
                vote = vote,
                voteItems = voteItems,
                voteMembers = voteMembers,
                replaceOriginal = true,
                type = VOTE,
                userId = user.id.toLong(),
            )
        } ?: throw IllegalStateException()
    }

    private fun VoteUpdateRequest.endVote(): CommandResponse {
        val vote = blindVoteRepository.findByIdOrNull(voteNo) ?: throw NotFoundException()
        val voteItems = blindVoteItemRepository.findByVoteVoteNo(vote.voteNo)
        val voteMembers = blindVoteMemberRepository.findByVoteVoteNo(vote.voteNo)

        return if (user.id == "${vote.userId}") {
            CommandResponse.createVoteBy(
                vote = vote,
                voteItems = voteItems,
                voteMembers = voteMembers,
                deleteOriginal = true,
                type = END_VOTE,
                userId = user.id.toLong(),
            )
        } else {
            CommandResponse.createResponse(
                text = "본인의 투표만 종료할 수 있어요. 🥸",
                replaceOriginal = false,
            )
        }
    }

    private fun VoteUpdateRequest.openDialog(
        title: String,
        submitLabel: String,
        type: VoteInteractionType,
    ) {
        runBlocking {
            doorayClient.openDialog(
                tenantDomain = tenant.domain,
                channelId = channel.id,
                cmdToken = cmdToken,
                request = DialogRequest(
                    token = cmdToken,
                    triggerId = triggerId,
                    callbackId = callbackId,
                    dialog = DoorayDialog(
                        callbackId = callbackId,
                        title = title,
                        submitLabel = submitLabel,
                        elements = listOf(
                            DoorayElement(
                                name = type.name,
                            ),
                        ),
                    ),
                ),
            )
        }
    }
}
