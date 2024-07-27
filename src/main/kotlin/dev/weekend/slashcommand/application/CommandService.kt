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
            userId = createRequest.userId,
            tenantId = createRequest.tenantId,
        ).let { blindVoteRepository.save(it) }

        return CommandResponse.createFormBy(
            vote = vote,
        )
    }

    fun updateBlindVote(
        request: VoteUpdateRequest,
    ): CommandResponse {
        return when (request.actionName ?: request.submission.keys.first()) {
            OPEN_TITLE_CHANGE_DIALOG -> request.openTitleChangeDialog()
            CHANGE_TITLE -> request.changeTitle()
            OPEN_ITEM_CHANGE_DIALOG -> request.openItemChangeDialog()
            CHANGE_ITEM -> request.changeItem()
            OPEN_ITEM_ADD_DIALOG -> request.openItemAddDialog()
            ADD_ITEM -> request.addItem()
            CHANGE_SELECTABLE_ITEM_COUNT -> request.changeSelectableItemCount()
            START_VOTE -> request.startVote()
            CANCEL_VOTE -> request.cancelVote()
            CHECK_VOTE -> request.checkVote()
            VOTE -> request.vote()
            END_VOTE -> request.endVote()
        }
    }

    private fun VoteUpdateRequest.openTitleChangeDialog(): CommandResponse {
        openDialog(
            title = "제목 수정",
            submitLabel = "저장",
            type = CHANGE_TITLE,
        )

        return CommandResponse.createResponse(
            text = "제목 수정 버튼을 눌렀을 때~",
            replaceOriginal = true,
        )
    }

    private fun VoteUpdateRequest.changeTitle(): CommandResponse {
        transactionTemplate.executeWithoutResult {
            val vote = blindVoteRepository.findByIdOrNull(voteNo) ?: throw NotFoundException()

            vote.updateTitle(submission.getValue(CHANGE_TITLE))
        }

        return CommandResponse.createResponse(
            text = "수정할 제목을 입력했을때",
            replaceOriginal = true,
        )
    }

    private fun VoteUpdateRequest.openItemAddDialog(): CommandResponse {
        openDialog(
            title = "항목 추가",
            submitLabel = "저장",
            type = ADD_ITEM,
        )

        return CommandResponse.createResponse(
            text = "항목 추가 버튼을 눌렀을 대",
            replaceOriginal = true,
        )
    }

    private fun VoteUpdateRequest.addItem(): CommandResponse {
        transactionTemplate.executeWithoutResult {
            val vote = blindVoteRepository.findByIdOrNull(voteNo) ?: throw NotFoundException()
            val voteItems = blindVoteItemRepository.findByVoteVoteNo(vote.voteNo).toMutableList()

            BlindVoteItem.createBy(
                vote = vote,
                voteItemName = submission.getValue(ADD_ITEM),
            ).let { blindVoteItemRepository.save(it) }
                .also { voteItems.add(it) }
            vote.updateSelectableItemCnt(voteItems.size)
        }

        return CommandResponse.createResponse(
            text = "추가할 항목을 입력했을 때",
            replaceOriginal = true,
        )
    }

    private fun VoteUpdateRequest.openItemChangeDialog(): CommandResponse {
        openDialog(
            title = "항목 수정",
            submitLabel = "저장",
            type = CHANGE_ITEM,
        )

        return CommandResponse.createResponse(
            text = "항목 변경 버튼을 눌렀을때",
            replaceOriginal = true,
        )
    }

    private fun VoteUpdateRequest.changeItem(): CommandResponse {
        transactionTemplate.executeWithoutResult {
            val vote = blindVoteRepository.findByIdOrNull(voteNo) ?: throw NotFoundException()
            val voteItems = blindVoteItemRepository.findByVoteVoteNo(vote.voteNo).toMutableList()

            voteItems.first { it.voteItemNo == voteItemNo }
                .updateName(submission.getValue(CHANGE_ITEM))
        }

        return CommandResponse.createResponse(
            text = "변경할 항목을 입력했을 때",
            replaceOriginal = true,
        )
    }

    private fun VoteUpdateRequest.changeSelectableItemCount(): CommandResponse {
        return transactionTemplate.execute {
            val vote = blindVoteRepository.findByIdOrNull(voteNo) ?: throw NotFoundException()
            val voteItems = blindVoteItemRepository.findByVoteVoteNo(vote.voteNo)
            val selectableItemCount = actionValue?.toInt() ?: 0

            vote.updateSelectableItemCnt(selectableItemCount)

            CommandResponse.createFormBy(
                vote = vote,
                voteItems = voteItems,
            )
        } ?: CommandResponse.createResponse()
    }

    private fun VoteUpdateRequest.startVote(): CommandResponse {
        val vote = blindVoteRepository.findByIdOrNull(voteNo) ?: throw NotFoundException()
        val voteItems = blindVoteItemRepository.findByVoteVoteNo(vote.voteNo)

        return when {
            vote.voteTitle.isEmpty() -> CommandResponse.createResponse(
                text = "투표 제목을 입력해 주세요. 🥸",
                replaceOriginal = false,
            )

            voteItems.isEmpty() -> CommandResponse.createResponse(
                text = "투표 항목을 추가해 주세요. 🥸",
                replaceOriginal = false,
            )

            else -> CommandResponse.createVoteBy(
                vote = vote,
                voteItems = voteItems,
                deleteOriginal = true,
                type = START_VOTE,
                userId = user.id.toLong(),
            )
        }
    }

    private fun VoteUpdateRequest.cancelVote(): CommandResponse {
        return CommandResponse.createCancelVote()
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

            val targetItem = voteItems.first { it.voteItemNo == actionValue?.toLong() }
            val targetMember = voteMembers.firstOrNull {
                it.userId == user.id.toLong() && it.voteItem.voteItemNo == targetItem.voteItemNo
            }

            if (targetMember == null) {
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
                targetItem.decreaseCnt()
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
        } ?: CommandResponse.createResponse()
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
                        callbackId = "$callbackId:$actionValue",
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
