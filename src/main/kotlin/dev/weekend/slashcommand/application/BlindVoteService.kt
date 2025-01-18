package dev.weekend.slashcommand.application

import dev.weekend.slashcommand.application.model.DialogRequest
import dev.weekend.slashcommand.domain.entity.BlindVote
import dev.weekend.slashcommand.domain.entity.BlindVoteItem
import dev.weekend.slashcommand.domain.entity.BlindVoteMember
import dev.weekend.slashcommand.domain.enums.DoorayResponseType.EPHEMERAL
import dev.weekend.slashcommand.domain.enums.DoorayResponseType.IN_CHANNEL
import dev.weekend.slashcommand.domain.enums.VoteInteractionType
import dev.weekend.slashcommand.domain.enums.VoteInteractionType.*
import dev.weekend.slashcommand.domain.model.DoorayDialog
import dev.weekend.slashcommand.domain.model.DoorayElement
import dev.weekend.slashcommand.domain.repository.BlindVoteEmojiRepository
import dev.weekend.slashcommand.domain.repository.BlindVoteItemRepository
import dev.weekend.slashcommand.domain.repository.BlindVoteMemberRepository
import dev.weekend.slashcommand.domain.repository.BlindVoteRepository
import dev.weekend.slashcommand.infrastructure.client.DoorayClient
import dev.weekend.slashcommand.presentation.model.CommandResponse
import dev.weekend.slashcommand.presentation.model.VoteCreateRequest
import dev.weekend.slashcommand.presentation.model.VoteInteractRequest
import kotlinx.coroutines.runBlocking
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.support.TransactionTemplate

/**
 * @author Jaeguk Cho
 */

@Service
class BlindVoteService(
    private val blindVoteRepository: BlindVoteRepository,
    private val blindVoteItemRepository: BlindVoteItemRepository,
    private val blindVoteMemberRepository: BlindVoteMemberRepository,
    private val blindVoteEmojiRepository: BlindVoteEmojiRepository,
    private val doorayClient: DoorayClient,
    private val transactionTemplate: TransactionTemplate,
) {
    fun createBlindVote(
        request: VoteCreateRequest,
    ): CommandResponse {
        val emoji = blindVoteEmojiRepository.getRandomEmoji()
        val vote = BlindVote.createBy(
            emoji = emoji,
            userId = request.userId,
            tenantId = request.tenantId,
            responseUrl = request.responseUrl,
        ).let { blindVoteRepository.save(it) }

        return CommandResponse.createFormBy(
            vote = vote,
        )
    }

    fun interactBlindVote(
        request: VoteInteractRequest,
    ): CommandResponse {
        return when (request.actionName ?: request.submission.keys.first()) {
            OPEN_TITLE_CHANGE_DIALOG -> request.openTitleChangeDialog()
            CHANGE_TITLE -> request.changeTitle()
            OPEN_ITEM_CHANGE_DIALOG -> request.openItemChangeDialog()
            CHANGE_ITEM -> request.changeItem()
            OPEN_ITEM_ADD_DIALOG -> request.openItemAddDialog()
            ADD_ITEM -> request.addItem()
            CHANGE_SELECTABLE_ITEM_COUNT -> request.changeSelectableItemCount()
            CHANGE_SHOW_PROGRESS_YN -> request.changeShowProgressYn()
            START_VOTE -> request.startVote()
            CANCEL_VOTE -> request.cancelVote()
            CHECK_VOTE -> request.checkVote()
            VOTE -> request.vote()
            END_VOTE -> request.endVote()
            else -> throw IllegalStateException()
        }
    }

    private fun VoteInteractRequest.openTitleChangeDialog(): CommandResponse {
        transactionTemplate.executeWithoutResult {
            val vote = blindVoteRepository.findByIdOrNull(voteNo) ?: throw NotFoundException()

            vote.updateResponseUrl(responseUrl)

            openDialog(
                title = "제목 수정",
                type = CHANGE_TITLE,
                value = vote.voteTitle,
                linkValue = vote.voteLink,
                label = "제목",
            )
        }

        return CommandResponse.createResponse()
    }

    private fun VoteInteractRequest.changeTitle(): CommandResponse {
        transactionTemplate.executeWithoutResult {
            val vote = blindVoteRepository.findByIdOrNull(voteNo) ?: throw NotFoundException()
            val voteItems = blindVoteItemRepository.findByVoteVoteNo(vote.voteNo)

            vote.updateTitle(voteTitle)
            vote.updateLink(voteLink.takeIf { !it.isNullOrEmpty() })
            voteItems.forEach {
                it.updateVoteTitle(voteTitle)
            }

            runBlocking {
                doorayClient.sendHook(
                    uri = vote.responseUrl,
                    body = CommandResponse.createFormBy(
                        vote = vote,
                        voteItems = voteItems,
                        replaceOriginal = true,
                        channelId = channel.id,
                    ),
                )
            }
        }

        return CommandResponse.createResponse()
    }

    private fun VoteInteractRequest.openItemAddDialog(): CommandResponse {
        transactionTemplate.executeWithoutResult {
            val vote = blindVoteRepository.findByIdOrNull(voteNo) ?: throw NotFoundException()

            vote.updateResponseUrl(responseUrl)

            openDialog(
                title = "항목 추가",
                type = ADD_ITEM,
                label = "항목",
            )
        }

        return CommandResponse.createResponse()
    }

    private fun VoteInteractRequest.addItem(): CommandResponse {
        transactionTemplate.executeWithoutResult {
            val vote = blindVoteRepository.findByIdOrNull(voteNo) ?: throw NotFoundException()
            val voteItems = blindVoteItemRepository.findByVoteVoteNo(vote.voteNo).toMutableList()

            BlindVoteItem.createBy(
                vote = vote,
                voteItemName = voteItem,
                voteItemLink = voteLink.takeIf { !it.isNullOrEmpty() },
            ).let { blindVoteItemRepository.save(it) }.also { voteItems.add(it) }
            vote.updateSelectableItemCnt(voteItems.size)

            runBlocking {
                doorayClient.sendHook(
                    uri = vote.responseUrl,
                    body = CommandResponse.createFormBy(
                        vote = vote,
                        voteItems = voteItems,
                        replaceOriginal = true,
                        channelId = channel.id,
                    ),
                )
            }
        }

        return CommandResponse.createResponse()
    }

    private fun VoteInteractRequest.openItemChangeDialog(): CommandResponse {
        transactionTemplate.executeWithoutResult {
            val vote = blindVoteRepository.findByIdOrNull(voteNo) ?: throw NotFoundException()
            val voteItem = blindVoteItemRepository.findByIdOrNull(actionValue?.toLong()) ?: throw NotFoundException()

            vote.updateResponseUrl(responseUrl)

            openDialog(
                title = "항목 수정",
                type = CHANGE_ITEM,
                value = voteItem.voteItemName,
                linkValue = voteItem.voteItemLink,
                label = "항목",
            )
        }

        return CommandResponse.createResponse()
    }

    private fun VoteInteractRequest.changeItem(): CommandResponse {
        transactionTemplate.executeWithoutResult {
            val vote = blindVoteRepository.findByIdOrNull(voteNo) ?: throw NotFoundException()
            val voteItems = blindVoteItemRepository.findByVoteVoteNo(vote.voteNo).toMutableList()

            voteItems.first { it.voteItemNo == voteItemNo }.apply {
                updateName(voteItem)
                updateLink(voteLink.takeIf { !it.isNullOrEmpty() })
            }

            runBlocking {
                doorayClient.sendHook(
                    uri = vote.responseUrl,
                    body = CommandResponse.createFormBy(
                        vote = vote,
                        voteItems = voteItems,
                        replaceOriginal = true,
                        channelId = channel.id,
                    ),
                )
            }
        }

        return CommandResponse.createResponse()
    }

    private fun VoteInteractRequest.changeSelectableItemCount(): CommandResponse {
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

    private fun VoteInteractRequest.changeShowProgressYn(): CommandResponse {
        return transactionTemplate.execute {
            val vote = blindVoteRepository.findByIdOrNull(voteNo) ?: throw NotFoundException()
            val voteItems = blindVoteItemRepository.findByVoteVoteNo(vote.voteNo)
            val showProgress = actionValue != "N"

            vote.updateShowProgress(showProgress)

            CommandResponse.createFormBy(
                vote = vote,
                voteItems = voteItems,
            )
        } ?: CommandResponse.createResponse()
    }

    private fun VoteInteractRequest.startVote(): CommandResponse {
        val vote = blindVoteRepository.findByIdOrNull(voteNo) ?: throw NotFoundException()
        val voteItems = blindVoteItemRepository.findByVoteVoteNo(vote.voteNo)

        return when {
            vote.voteTitle.isNullOrEmpty() -> CommandResponse.createResponse(
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
                responseType = IN_CHANNEL,
                deleteOriginal = true,
                type = START_VOTE,
                userId = userId,
            )
        }
    }

    private fun VoteInteractRequest.cancelVote(): CommandResponse {
        return CommandResponse.createCancelVote()
    }

    private fun VoteInteractRequest.checkVote(): CommandResponse {
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
            userId = userId,
        )
    }

    private fun VoteInteractRequest.vote(): CommandResponse {
        return transactionTemplate.execute {
            val vote = blindVoteRepository.findByIdOrNull(voteNo) ?: throw NotFoundException()
            val voteItems = blindVoteItemRepository.findByVoteVoteNo(vote.voteNo)
            val voteMembers = blindVoteMemberRepository.findByVoteVoteNo(vote.voteNo).toMutableList()

            // 투표하려는 항목
            val targetItem = voteItems.first { it.voteItemNo == actionValue?.toLong() }

            // 해당 항목에 투표한 나의 표 (투표하지 않았다면 NULL)
            val targetMember = voteMembers.firstOrNull {
                it.userId == userId && it.voteItem.voteItemNo == targetItem.voteItemNo
            }

            if (targetMember == null) { // 해당 항목에 투표하지 않은 경우
                val selectedItemCount = voteMembers.count { it.userId == userId }

                if (vote.selectableItemCnt > selectedItemCount) { // 투표할 수 있는 개수보다 적게 투표한 경우
                    targetItem.increaseCnt()
                    BlindVoteMember.createBy(
                        vote = vote,
                        voteItem = targetItem,
                        userId = userId,
                    ).let { blindVoteMemberRepository.save(it) }.also { voteMembers.add(it) }
                } else if (vote.selectableItemCnt == 1) { // 투표할 수 있는 개수가 1개이고, 나의 투표수도 1인 경우
                    val previousVoteMember = voteMembers.first { it.userId == userId }
                    val previousVoteItem = voteItems.first { it.voteItemNo == previousVoteMember.voteItem.voteItemNo }
                    previousVoteItem.decreaseCnt()
                    blindVoteMemberRepository.delete(previousVoteMember)
                    voteMembers.remove(previousVoteMember)

                    targetItem.increaseCnt()
                    BlindVoteMember.createBy(
                        vote = vote,
                        voteItem = targetItem,
                        userId = userId,
                    ).let { blindVoteMemberRepository.save(it) }.also { voteMembers.add(it) }
                }
            } else { // 해당 항목에 이미 투표한 경우
                targetItem.decreaseCnt()
                blindVoteMemberRepository.delete(targetMember)
                voteMembers.remove(targetMember)
            }

            CommandResponse.createVoteBy(
                vote = vote,
                voteItems = voteItems,
                voteMembers = voteMembers,
                responseType = IN_CHANNEL,
                replaceOriginal = true,
                type = VOTE,
                userId = userId,
            )
        } ?: CommandResponse.createResponse()
    }

    private fun VoteInteractRequest.endVote(): CommandResponse {
        val vote = blindVoteRepository.findByIdOrNull(voteNo) ?: throw NotFoundException()
        val voteItems = blindVoteItemRepository.findByVoteVoteNo(vote.voteNo)
        val voteMembers = blindVoteMemberRepository.findByVoteVoteNo(vote.voteNo)

        return if (user.id == "${vote.userId}") {
            CommandResponse.createVoteBy(
                vote = vote,
                voteItems = voteItems,
                voteMembers = voteMembers,
                responseType = IN_CHANNEL,
                deleteOriginal = true,
                type = END_VOTE,
                userId = userId,
            )
        } else {
            CommandResponse.createResponse(
                text = "본인의 투표만 종료할 수 있어요. 🥸",
                replaceOriginal = false,
            )
        }
    }

    private fun VoteInteractRequest.openDialog(
        title: String,
        type: VoteInteractionType,
        value: String? = null,
        linkValue: String? = null,
        label: String,
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
                        elements = listOf(
                            DoorayElement(
                                label = "$label (필수)",
                                name = type.name,
                                value = value,
                                placeholder = "투표 ${label}을 입력해 주세요.",
                            ),
                            DoorayElement(
                                subType = "url",
                                label = "링크 (선택)",
                                name = "LINK",
                                value = linkValue,
                                placeholder = "클릭 시 이동할 링크를 입력해 주세요.",
                                optional = true,
                            ),
                        ),
                    ),
                ),
            )
        }
    }
}
