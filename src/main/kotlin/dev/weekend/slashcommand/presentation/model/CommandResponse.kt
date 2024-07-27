package dev.weekend.slashcommand.presentation.model

import dev.weekend.slashcommand.domain.entity.BlindVote
import dev.weekend.slashcommand.domain.entity.BlindVoteItem
import dev.weekend.slashcommand.domain.entity.BlindVoteMember
import dev.weekend.slashcommand.domain.enums.DoorayActionType.SELECT
import dev.weekend.slashcommand.domain.enums.DoorayButtonStyle.PRIMARY
import dev.weekend.slashcommand.domain.enums.DoorayResponseType.EPHEMERAL
import dev.weekend.slashcommand.domain.enums.DoorayResponseType.IN_CHANNEL
import dev.weekend.slashcommand.domain.enums.VoteInteractionType
import dev.weekend.slashcommand.domain.enums.VoteInteractionType.*
import dev.weekend.slashcommand.domain.model.DoorayAction
import dev.weekend.slashcommand.domain.model.DoorayAttachment
import dev.weekend.slashcommand.domain.model.DoorayField
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
) {
    companion object {
        private val emojiList = listOf(
            "🌝",
            "👻",
            "👍",
            "🐶",
            "🔥",
            "🍎",
            "⚽️",
            "❤️",
            "🌸",
            "👽",
            "👾",
        )

        fun createCancelVote() = CommandResponse(
            text = "투표 생성을 취소했습니다. 🥺",
            responseType = EPHEMERAL.value,
            deleteOriginal = true,
        )

        fun createFormBy(
            vote: BlindVote,
            voteItems: List<BlindVoteItem>,
            replaceOriginal: Boolean? = null,
            deleteOriginal: Boolean? = null,
        ) = CommandResponse(
            text = "무기명 투표 생성 중",
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
                            name = CHANGE_TITLE,
                            text = "수정",
                            value = "변경된 타이틀~", // 어떻게 입력받지
                        ),
                    ),
                ),
                DoorayAttachment(
                    callbackId = "${vote.voteNo}",
                    title = "항목",
                    actions = voteItems.map {
                        DoorayAction.createButton(
                            name = CHANGE_ITEM,
                            text = it.voteItemName,
                            value = "수정된 항목" + it.voteItemNo, // 입력받기 (1개씩)
                        )
                    } + DoorayAction.createButton(
                        name = ADD_ITEM,
                        text = "+",
                        value = "새로운 값을 추가해주세요이",
                    ),
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
                ),
            ),
        )

        fun createVoteBy(
            vote: BlindVote,
            voteItems: List<BlindVoteItem>,
            voteMembers: List<BlindVoteMember>,
            replaceOriginal: Boolean? = null,
            deleteOriginal: Boolean? = null,
            type: VoteInteractionType,
        ): CommandResponse {
            val emoji = emojiList[vote.voteNo.toInt() % emojiList.size]

            return CommandResponse(
                text = when (type) {
                    END_VOTE -> """(dooray://${vote.tenantId}/members/${vote.userId} "member") 님이 투표를 종료했습니다!"""
                    else -> """(dooray://${vote.tenantId}/members/${vote.userId} "member") 님이 투표를 생성했습니다!"""
                },
                responseType = IN_CHANNEL.value,
                replaceOriginal = replaceOriginal,
                deleteOriginal = deleteOriginal,
                attachments = listOfNotNull(
                    DoorayAttachment(
                        callbackId = "${vote.voteNo}",
                        title = vote.voteTitle,
                        text = "최대 ${vote.selectableItemCnt}개까지 고를 수 있습니다.",
                        actions = voteItems.map {
                            DoorayAction.createButton(
                                name = VOTE,
                                text = it.voteItemName,
                                value = "${it.voteItemNo}",
                            )
                        },
                    ).takeIf { type != END_VOTE },
                    DoorayAttachment(
                        callbackId = "${vote.voteNo}",
                        fields = voteItems.map {
                            DoorayField(
                                title = it.voteItemName,
                                value = emoji.repeat(it.voteCnt).takeIf { it.isNotEmpty() } ?: " ",
                            )
                        },
                    ),
                    DoorayAttachment(
                        callbackId = "${vote.voteNo}",
                        title = "총 투표자 수: " + voteMembers.distinctBy { it.userId }.size,
                    ),
                    DoorayAttachment(
                        callbackId = "${vote.voteNo}",
                        actions = listOf(
                            DoorayAction.createButton(
                                name = END_VOTE,
                                text = "투표 종료!",
                            )
                        )
                    ).takeIf { type != END_VOTE },
                )
            )
        }

        fun createTempResponse(
            actionName: VoteInteractionType,
            request: String,
        ) = CommandResponse(
            text = actionName.name,
            responseType = EPHEMERAL.value,
            attachments = listOf(
                DoorayAttachment(
                    callbackId = "temp",
                    fields = listOf(
                        DoorayField(
                            title = "제목",
                            value = request,
                        ),
                    ),
                ),
            ),
        )

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
