package dev.weekend.slashcommand.presentation.model

import dev.weekend.slashcommand.domain.constant.AkinatorConstant.DEFAULT_AKINATOR_IMAGE
import dev.weekend.slashcommand.domain.constant.AkinatorConstant.FIRST_STEP
import dev.weekend.slashcommand.domain.constant.MbtiConstant.DEFAULT_IMAGE
import dev.weekend.slashcommand.domain.constant.MbtiConstant.FIRST_QUESTION_SEQ
import dev.weekend.slashcommand.domain.constant.MbtiConstant.LAST_QUESTION_SEQ
import dev.weekend.slashcommand.domain.entity.*
import dev.weekend.slashcommand.domain.enums.AkinatorAnswerType
import dev.weekend.slashcommand.domain.enums.AkinatorInteractionType.*
import dev.weekend.slashcommand.domain.enums.DoorayActionType.SELECT
import dev.weekend.slashcommand.domain.enums.DoorayButtonStyle.DEFAULT
import dev.weekend.slashcommand.domain.enums.DoorayButtonStyle.PRIMARY
import dev.weekend.slashcommand.domain.enums.DoorayResponseType
import dev.weekend.slashcommand.domain.enums.DoorayResponseType.EPHEMERAL
import dev.weekend.slashcommand.domain.enums.DoorayResponseType.IN_CHANNEL
import dev.weekend.slashcommand.domain.enums.MbtiInteractionType.*
import dev.weekend.slashcommand.domain.enums.VoteInteractionType
import dev.weekend.slashcommand.domain.enums.VoteInteractionType.*
import dev.weekend.slashcommand.domain.extension.toGraphBar
import dev.weekend.slashcommand.domain.extension.toMonospacedFont
import dev.weekend.slashcommand.domain.extension.toPercent
import dev.weekend.slashcommand.domain.model.DoorayAction
import dev.weekend.slashcommand.domain.model.DoorayAttachment
import dev.weekend.slashcommand.domain.model.DoorayOption
import org.eu.zajc.akiwrapper.core.entities.impl.GuessImpl
import org.eu.zajc.akiwrapper.core.entities.impl.QuestionImpl

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

        fun createCancelTest() = CommandResponse(
            text = "검사를 취소했습니다. 🥺",
            responseType = EPHEMERAL.value,
            deleteOriginal = true,
        )

        fun createCancelAkinator() = CommandResponse(
            text = "아키네이터를 취소했습니다. 🥺",
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

        fun createFormBy(
            mbtiResult: MbtiResult?,
            mbtiDetail: MbtiDetail?,
        ) = CommandResponse(
            text = "당신의 MBTI: ${mbtiResult?.mbti?.boldText ?: "🤔"}",
            responseType = EPHEMERAL.value,
            attachments = listOf(
                DoorayAttachment(
                    text = "30초만에 나의 MBTI 검사하기",
                    imageUrl = mbtiDetail?.imageUrl ?: DEFAULT_IMAGE,
                ),
                DoorayAttachment(
                    actions = listOfNotNull(
                        DoorayAction.createButton(
                            name = START_TEST,
                            text = if (mbtiResult?.mbti == null) {
                                "검사하기"
                            } else {
                                "다시 검사하기"
                            },
                            style = PRIMARY,
                        ),
                        DoorayAction.createButton(
                            name = SHARE_RESULT,
                            text = "공유하기",
                        ).takeIf { mbtiResult != null },
                        DoorayAction.createButton(
                            name = GET_STATISTICS,
                            text = "통계 보기",
                        ).takeIf { mbtiResult != null },
                        DoorayAction.createButton(
                            name = CANCEL_TEST,
                            text = "취소",
                        ),
                    ),
                ),
            ),
        )

        fun createQuestionBy(
            mbtiTest: MbtiTest,
        ) = CommandResponse(
            text = "Q${mbtiTest.question.seq}. ${mbtiTest.question.question}",
            responseType = EPHEMERAL.value,
            attachments = listOf(
                DoorayAttachment(
                    actions = listOf(
                        DoorayAction.createButton(
                            name = FIRST_ANSWER,
                            text = mbtiTest.question.firstChoice.answer,
                            value = "${mbtiTest.testNo}:${mbtiTest.question.seq}",
                            style = when (mbtiTest.answer?.trait == mbtiTest.question.firstChoice.trait) {
                                true -> PRIMARY
                                false -> DEFAULT
                            },
                        ),
                    ),
                ),
                DoorayAttachment(
                    actions = listOf(
                        DoorayAction.createButton(
                            name = SECOND_ANSWER,
                            text = mbtiTest.question.secondChoice.answer,
                            value = "${mbtiTest.testNo}:${mbtiTest.question.seq}",
                            style = when (mbtiTest.answer?.trait == mbtiTest.question.secondChoice.trait) {
                                true -> PRIMARY
                                false -> DEFAULT
                            },
                        ),
                    ),
                ),
                DoorayAttachment(
                    actions = listOfNotNull(
                        DoorayAction.createButton(
                            name = when (mbtiTest.question.seq) {
                                FIRST_QUESTION_SEQ -> RESTART_TEST
                                else -> PREV_QUESTION
                            },
                            text = "이전",
                            value = "${mbtiTest.testNo}:${mbtiTest.question.seq}",
                        ),
                        DoorayAction.createButton(
                            name = when (mbtiTest.question.seq) {
                                LAST_QUESTION_SEQ -> GET_RESULT
                                else -> NEXT_QUESTION
                            },
                            text = when (mbtiTest.question.seq) {
                                LAST_QUESTION_SEQ -> "결과 보기"
                                else -> "다음"
                            },
                            value = "${mbtiTest.testNo}:${mbtiTest.question.seq}",
                        ).takeIf { mbtiTest.answer != null },
                    ),
                ),
            ),
        )

        fun createResultBy(
            mbtiResult: MbtiResult,
            mbtiDetail: MbtiDetail,
            responseType: DoorayResponseType,
            deleteOriginal: Boolean? = null,
            tenantId: String? = null,
            userId: String? = null,
        ) = CommandResponse(
            text = when (responseType) {
                IN_CHANNEL -> """(dooray://${tenantId}/members/${userId} "member") 님의 MBTI: ${mbtiResult.mbti.boldText}"""
                EPHEMERAL -> "당신의 MBTI: ${mbtiResult.mbti.boldText}"
            },
            responseType = responseType.value,
            deleteOriginal = deleteOriginal,
            attachments = listOfNotNull(
                DoorayAttachment(
                    title = mbtiDetail.alias,
                    titleLink = mbtiDetail.url,
                    imageUrl = mbtiDetail.imageUrl,
                ),
                DoorayAttachment(
                    actions = listOf(
                        DoorayAction.createButton(
                            name = RESTART_TEST,
                            text = "다시 검사하기",
                        ),
                        DoorayAction.createButton(
                            name = SHARE_RESULT,
                            text = "공유하기",
                        ),
                        DoorayAction.createButton(
                            name = GET_STATISTICS,
                            text = "통계 보기",
                        ),
                    ),
                ).takeIf { responseType == EPHEMERAL },
            ),
        )

        fun createStatisticsBy(
            mbtiResults: List<MbtiResult>,
            totalCount: Int,
            responseType: DoorayResponseType,
            deleteOriginal: Boolean? = null,
        ): CommandResponse {
            val results = mbtiResults.groupBy { it.mbti }.mapValues { it.value.size - 1 }.toList()
                .sortedByDescending { it.second }

            return CommandResponse(
                text = "NHN 임직원의 MBTI 분포는? 🧐\n\n" + results.joinToString("\n") {
                    val percent = it.second.toPercent(totalCount)

                    "${it.first}: ${percent.toGraphBar()} ${percent}% (${it.second}명)"
                }.toMonospacedFont(),
                responseType = responseType.value,
                deleteOriginal = deleteOriginal,
                attachments = listOfNotNull(
                    DoorayAttachment(
                        actions = listOf(
                            DoorayAction.createButton(
                                name = SHARE_STATISTICS,
                                text = "공유하기",
                            ),
                        ),
                    ).takeIf { responseType == EPHEMERAL },
                ),
            )
        }

        fun createFormBy(
            akinatorExists: Boolean,
        ) = CommandResponse(
            text = when (akinatorExists) {
                true -> "이어서 진행하시겠습니까?"
                false -> "실제 또는 가상의 인물을 생각해 보세요."
            },
            responseType = EPHEMERAL.value,
            attachments = listOf(
                DoorayAttachment(
                    imageUrl = DEFAULT_AKINATOR_IMAGE,
                ),
                DoorayAttachment(
                    actions = when (akinatorExists) {
                        true -> listOf(
                            DoorayAction.createButton(
                                name = CONTINUE_AKINATOR,
                                text = "이어서 시작",
                                style = DEFAULT,
                            ),
                            DoorayAction.createButton(
                                name = START_AKINATOR,
                                text = "새로 시작",
                                style = DEFAULT,
                            ),
                        )

                        false -> listOf(
                            DoorayAction.createButton(
                                name = START_AKINATOR,
                                text = "시작",
                                style = PRIMARY,
                            ),
                        )
                    } + listOf(
                        DoorayAction.createButton(
                            name = CANCEL_AKINATOR,
                            text = "취소",
                        ),
                    ),
                ),
            ),
        )

        fun createQuestionBy(
            question: QuestionImpl,
        ) = CommandResponse(
            text = "Q${question.step + 1}. ${question.text}",
            attachments = listOfNotNull(
                DoorayAttachment(
                    imageUrl = question.akitude.toString(),
                    actions = AkinatorAnswerType.entries.map {
                        DoorayAction.createButton(
                            name = ANSWER_QUESTION,
                            text = it.value,
                            value = it.name,
                        )
                    },
                ),
                DoorayAttachment(
                    actions = listOf(
                        DoorayAction.createButton(
                            name = UNDO_ANSWER,
                            text = "이전 질문",
                        ),
                    ),
                ).takeIf { question.step != FIRST_STEP },
            ),
            responseType = EPHEMERAL.value,
        )

        fun createGuessBy(
            guess: GuessImpl,
        ) = CommandResponse(
            text = "제 생각은..",
            responseType = EPHEMERAL.value,
            attachments = listOf(
                DoorayAttachment(
                    title = guess.name,
                    authorName = guess.description,
                    text = guess.pseudonym,
                    imageUrl = guess.image.toString(),
                    actions = listOf(
                        DoorayAction.createButton(
                            name = CONFIRM_GUESS,
                            text = "맞아요",
                        ),
                        DoorayAction.createButton(
                            name = REJECT_GUESS,
                            text = "아니에요",
                        ),
                    ),
                ),
            ),
        )

        fun createResultBy(
            akinatorResult: AkinatorResult,
            responseType: DoorayResponseType,
            deleteOriginal: Boolean? = null,
        ) = CommandResponse(
            text = "훌륭합니다, 또 맞추었습니다!",
            responseType = responseType.value,
            deleteOriginal = deleteOriginal,
            attachments = listOf(
                DoorayAttachment(
                    title = akinatorResult.name,
                    authorName = akinatorResult.description,
                    text = akinatorResult.pseudonym,
                    imageUrl = akinatorResult.image.toString(),
                    actions = listOf(
                        DoorayAction.createButton(
                            name = SHARE_GUESS,
                            text = "공유하기",
                        ),
                    ).takeIf { responseType == EPHEMERAL },
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
