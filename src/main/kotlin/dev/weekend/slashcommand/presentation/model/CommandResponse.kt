package dev.weekend.slashcommand.presentation.model

import dev.weekend.slashcommand.domain.entity.*
import dev.weekend.slashcommand.domain.enums.DoorayActionType.SELECT
import dev.weekend.slashcommand.domain.enums.DoorayButtonStyle.DEFAULT
import dev.weekend.slashcommand.domain.enums.DoorayButtonStyle.PRIMARY
import dev.weekend.slashcommand.domain.enums.DoorayResponseType
import dev.weekend.slashcommand.domain.enums.DoorayResponseType.EPHEMERAL
import dev.weekend.slashcommand.domain.enums.VoteInteractionType
import dev.weekend.slashcommand.domain.enums.VoteInteractionType.*
import dev.weekend.slashcommand.domain.model.DoorayAction
import dev.weekend.slashcommand.domain.model.DoorayAttachment
import dev.weekend.slashcommand.domain.model.DoorayOption

/**
 * @author Jaeguk Cho
 */

data class CommandResponse(
    val text: String,
    val responseType: String,
    val replaceOriginal: Boolean? = null,
    val deleteOriginal: Boolean? = null,
    val attachments: List<DoorayAttachment>? = null,
    val channelId: String? = null,
    val creatorId: Long? = null,
) {
    companion object {
        fun createCancelVote() = CommandResponse(
            text = "투표 생성을 취소했습니다. 🥺",
            responseType = EPHEMERAL.value,
            deleteOriginal = true,
        )

        fun createFormBy(
            vote: BlindVote,
            voteItems: List<BlindVoteItem> = emptyList(),
            replaceOriginal: Boolean? = null,
            deleteOriginal: Boolean? = null,
            channelId: String? = null,
        ) = CommandResponse(
            text = "투표 생성 중",
            responseType = EPHEMERAL.value,
            replaceOriginal = replaceOriginal,
            deleteOriginal = deleteOriginal,
            attachments = listOf(
                DoorayAttachment(
                    callbackId = "${vote.voteNo}",
                    title = "투표 제목",
                    text = vote.voteTitle,
                    actions = listOf(
                        DoorayAction.createButton(
                            name = OPEN_TITLE_CHANGE_DIALOG,
                            text = "수정",
                        ),
                    ),
                    color = "black",
                ),
                DoorayAttachment(
                    callbackId = "${vote.voteNo}",
                    title = "항목",
                    actions = voteItems.map {
                        DoorayAction.createButton(
                            name = OPEN_ITEM_CHANGE_DIALOG,
                            text = it.voteItemName,
                            value = "${it.voteItemNo}",
                        )
                    } + DoorayAction.createButton(
                        name = OPEN_ITEM_ADD_DIALOG,
                        text = "+",
                    ),
                    color = "orange",
                ),
                DoorayAttachment(
                    callbackId = "${vote.voteNo}",
                    title = "선택 가능한 개수",
                    actions = listOf(
                        DoorayAction(
                            type = SELECT.value,
                            name = CHANGE_SELECTABLE_ITEM_COUNT,
                            text = "${vote.selectableItemCnt}",
                            options = (1..voteItems.size).map {
                                DoorayOption(
                                    text = "$it",
                                    value = "$it",
                                )
                            },
                        ),
                    ),
                    color = "black",
                ),
                DoorayAttachment(
                    callbackId = "${vote.voteNo}",
                    title = "투표 현황 공개 여부",
                    actions = listOf(
                        DoorayAction(
                            type = SELECT.value,
                            name = CHANGE_SHOW_PROGRESS_YN,
                            text = "공개",
                            options = listOf(
                                DoorayOption(
                                    text = "공개",
                                    value = "Y",
                                ),
                                DoorayOption(
                                    text = "비공개",
                                    value = "N",
                                ),
                            ),
                        ),
                    ),
                    color = "black",
                ),
                DoorayAttachment(
                    callbackId = "${vote.voteNo}",
                    actions = listOf(
                        DoorayAction.createButton(
                            name = START_VOTE,
                            text = "생성",
                            style = PRIMARY,
                        ),
                        DoorayAction.createButton(
                            name = CANCEL_VOTE,
                            text = "취소",
                        ),
                    ),
                    color = "black",
                ),
            ),
            channelId = channelId,
        )

        fun createVoteBy(
            vote: BlindVote,
            voteItems: List<BlindVoteItem>,
            voteMembers: List<BlindVoteMember> = emptyList(),
            responseType: DoorayResponseType,
            replaceOriginal: Boolean? = null,
            deleteOriginal: Boolean? = null,
            type: VoteInteractionType,
            userId: Long,
            channelId: String? = null, // 훅으로 보낼 땐 필수
        ): CommandResponse {
            val myVotes = voteMembers.filter { it.userId == userId }.map { it.voteItem.voteItemNo }
            val maxVoteCount = voteItems.maxOf { it.voteCnt }
            val isGoldMedal = { count: Int -> count > 0 && count == maxVoteCount }

            return CommandResponse(
                text = when (type) {
                    START_VOTE, VOTE -> """(dooray://${vote.tenantId}/members/${vote.userId} "member") 님이 투표를 생성했습니다!"""
                    END_VOTE -> """(dooray://${vote.tenantId}/members/${vote.userId} "member") 님이 투표를 종료했습니다!"""
                    CHECK_VOTE -> "선택한 항목이 아래에 파란색으로 표시됩니다."
                    else -> "허용되지 않는 상태"
                },
                responseType = responseType.value,
                replaceOriginal = replaceOriginal,
                deleteOriginal = deleteOriginal,
                attachments = listOfNotNull(
                    DoorayAttachment(
                        callbackId = "${vote.voteNo}",
                        text = "결과는 투표 종료 후 공개됩니다. 🤫",
                        color = "black",
                    ).takeIf { !vote.showProgress && type != END_VOTE },
                    DoorayAttachment(
                        callbackId = "${vote.voteNo}",
                        actions = listOf(
                            DoorayAction.createButton(
                                name = CHECK_VOTE,
                                text = "내 선택 확인하기",
                            )
                        ),
                        color = "black",
                    ).takeIf { type != END_VOTE },
                    DoorayAttachment(
                        title = vote.voteTitle,
                        titleLink = vote.voteLink,
                        color = "black",
                    ).takeIf { type == END_VOTE },
                    DoorayAttachment(
                        callbackId = "${vote.voteNo}",
                        title = vote.voteTitle,
                        titleLink = vote.voteLink,
                        text = "최대 ${vote.selectableItemCnt}개까지 고를 수 있습니다.",
                        actions = voteItems.map {
                            DoorayAction.createButton(
                                name = VOTE,
                                text = it.voteItemName,
                                value = "${it.voteItemNo}",
                                style = when {
                                    type == CHECK_VOTE && it.voteItemNo in myVotes -> PRIMARY
                                    else -> DEFAULT
                                },
                            )
                        },
                        color = "black",
                    ).takeIf { type != END_VOTE },
                ) + voteItems.let { items ->
                    when (type) {
                        END_VOTE -> items.sortedByDescending { it.voteCnt }
                        else -> items.sortedBy { it.voteItemNo }
                    }
                }.map { item ->
                    if (!vote.showProgress && type != END_VOTE) {
                        DoorayAttachment(
                            callbackId = "${vote.voteNo}:${item.voteItemNo}",
                            title = item.voteItemName,
                            titleLink = item.voteItemLink,
                            color = "orange",
                        )
                    } else {
                        DoorayAttachment(
                            callbackId = "${vote.voteNo}:${item.voteItemNo}",
                            title = item.voteItemName + " 🥇".takeIf { isGoldMedal(item.voteCnt) }.orEmpty(),
                            titleLink = item.voteItemLink,
                            text = "${vote.voteEmoji.emoji.repeat(item.voteCnt)} (${item.voteCnt})",
                            color = "orange",
                        )
                    }
                } + listOfNotNull(
                    DoorayAttachment(
                        callbackId = "${vote.voteNo}",
                        title = "총 투표자 수: " + voteMembers.distinctBy { it.userId }.size,
                        color = "black",
                    ),
                    DoorayAttachment(
                        callbackId = "${vote.voteNo}",
                        actions = listOf(
                            DoorayAction.createButton(
                                name = END_VOTE,
                                text = "투표 종료!",
                                displayTargets = listOf(
                                    "creator",
                                ),
                            )
                        ),
                        color = "black",
                    ).takeIf { type != END_VOTE },
                ),
                channelId = channelId,
                creatorId = vote.userId,
            )
        }

        fun createResponse(
            text: String = "",
            replaceOriginal: Boolean? = null,
            deleteOriginal: Boolean? = null,
            channelId: String? = null,
        ) = CommandResponse(
            text = text,
            responseType = EPHEMERAL.value,
            replaceOriginal = replaceOriginal,
            deleteOriginal = deleteOriginal,
            channelId = channelId,
        )
    }
}
