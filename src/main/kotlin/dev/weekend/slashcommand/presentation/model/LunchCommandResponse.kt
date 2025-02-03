package dev.weekend.slashcommand.presentation.model

import dev.weekend.slashcommand.application.model.LunchActionSummary
import dev.weekend.slashcommand.domain.constant.LunchConstant.CONFIRM_LIST
import dev.weekend.slashcommand.domain.constant.LunchConstant.HAPPY_EMOJIS
import dev.weekend.slashcommand.domain.constant.LunchConstant.RESET_EMOJIS
import dev.weekend.slashcommand.domain.constant.LunchConstant.SAD_EMOJIS
import dev.weekend.slashcommand.domain.entity.LunchItem
import dev.weekend.slashcommand.domain.enums.DoorayButtonStyle.PRIMARY
import dev.weekend.slashcommand.domain.enums.DoorayResponseType.EPHEMERAL
import dev.weekend.slashcommand.domain.enums.DoorayResponseType.IN_CHANNEL
import dev.weekend.slashcommand.domain.enums.LunchInteractionType
import dev.weekend.slashcommand.domain.enums.LunchItemType
import dev.weekend.slashcommand.domain.extension.getRandom
import dev.weekend.slashcommand.domain.extension.toJson
import dev.weekend.slashcommand.domain.model.DoorayAction
import dev.weekend.slashcommand.domain.model.DoorayAttachment

/**
 * @author Yoohwa Cho
 */
data class LunchCommandResponse(
    val text: String,
    val responseType: String? = null,
    val replaceOriginal: Boolean? = null,
    val deleteOriginal: Boolean? = null,
    val attachments: List<DoorayAttachment>? = null,
    val channelId: String? = null,
    val creatorId: Long? = null,
) {
    companion object {
        fun createLunchStartFormBy() = LunchCommandResponse(
            text = "오늘의 점심 메뉴는? 😮",
            responseType = EPHEMERAL.value,
            attachments = listOf(
                DoorayAttachment(
                    actions = listOfNotNull(
                        DoorayAction.createButton(
                            name = LunchInteractionType.START,
                            text = "혼자 고를래요 👤",
                            value = LunchActionSummary.createBy(EPHEMERAL).toJson()
                        ),
                        DoorayAction.createButton(
                            name = LunchInteractionType.START,
                            text = "같이 고를래요 👥",
                            value = LunchActionSummary.createBy(IN_CHANNEL).toJson(),
                        ),
                        DoorayAction.createButton(
                            name = LunchInteractionType.HELP,
                            text = "더보기 🔍",
                            value = LunchActionSummary.createBy(EPHEMERAL).toJson(),
                        ),
                    )
                )
            )
        )

        fun createLunchFormBy(summary: LunchActionSummary) = LunchCommandResponse(
            text = "오늘의 점심 메뉴는? 😮",
            responseType = summary.responseType,
            deleteOriginal = if (summary.isInChannel()) true else null,
            attachments = listOf(
                DoorayAttachment(
                    actions = listOfNotNull(
                        DoorayAction.createButton(
                            name = LunchInteractionType.GET_RECOMMENDATION,
                            text = "랜덤으로 추천받기",
                            style = PRIMARY,
                            value = summary.toJson(),
                        ),
                        DoorayAction.createButton(
                            name = LunchInteractionType.START_DETAIL_RECOMMEND,
                            text = "카테고리 선택하기",
                            value = summary.toJson(),
                        ),
                        DoorayAction.createButton(
                            name = LunchInteractionType.CANCEL,
                            text = "취소하기",
                            value = summary.toJson(),
                        ),
                    )
                )
            )
        )

        fun createLunchResultBy(item: LunchItem, summary: LunchActionSummary): LunchCommandResponse {
            return if (summary.isInChannel()) createPublicLunchResultBy(item, summary)
            else createPrivateLunchResultBy(item, summary)
        }

        //혼자 고르기 할때 추천
        private fun createPrivateLunchResultBy(item: LunchItem, summary: LunchActionSummary) = LunchCommandResponse(
            text = "오늘 점심으로 `${item.name}`(${item.type.label}) 어떠세요?",
            responseType = summary.responseType,
            replaceOriginal = true,
            attachments = listOf(
                DoorayAttachment(
                    title = "${item.name} - 메뉴 보러가기",
                    titleLink = item.link,
                ),
                DoorayAttachment(
                    actions = listOfNotNull(
                        DoorayAction.createButton(
                            name = LunchInteractionType.CONFIRM_RECOMMEND,
                            text = "공유하기 ${HAPPY_EMOJIS.getRandom()}",
                            value = summary.changeItemNo(item.no).toJson(),
                        ),
                        DoorayAction.createButton(
                            name = LunchInteractionType.RECOMMEND_AGAIN,
                            text = "${if (summary.itemType.isNotBlank()) item.type.label + " " else ""}다시 뽑기 ${
                                SAD_EMOJIS.getRandom()
                            }",
                            value = summary.toJson(),
                        ),
                        DoorayAction.createButton(
                            name = LunchInteractionType.RESTART,
                            text = "처음으로 ${RESET_EMOJIS.getRandom()}",
                            value = LunchActionSummary.createBy(summary.convertResponseType()).toJson()
                        ),
                    )
                )
            )
        )

        //같이 고르기 할때 추천
        private fun createPublicLunchResultBy(item: LunchItem, summary: LunchActionSummary) = LunchCommandResponse(
            text = "오늘 점심으로 `${item.name}`(${item.type.label}) 어떠세요?",
            responseType = summary.responseType,
            replaceOriginal = true,
            attachments = listOf(
//                DoorayAttachment(
//                    actions = listOfNotNull(
//                        DoorayAction.createButton(
//                            name = LunchInteractionType.LIKE,
//                            text = "좋아요 👍",
//                            value = summary.likeItem().toJson()
//                        ),
//                        DoorayAction.createButton(
//                            name = LunchInteractionType.LIKE,
//                            text = "싫어요 👎",
//                            value = summary.dislikeItem().toJson()
//                        ),
//                    )
//                ),
                DoorayAttachment(
                    title = "${item.name} - 메뉴 보러가기",
                    titleLink = item.link,
                ),
                DoorayAttachment(
                    actions = listOfNotNull(
                        DoorayAction.createButton(
                            name = LunchInteractionType.CONFIRM_RECOMMEND,
                            text = "확정하기 ${HAPPY_EMOJIS.getRandom()}",
                            value = summary.changeItemNo(item.no).toJson(),
                        ),
                        DoorayAction.createButton(
                            name = LunchInteractionType.RECOMMEND_AGAIN,
                            text = "${if (summary.itemType.isNotBlank()) item.type.label + " " else ""}다시 뽑기 ${SAD_EMOJIS.getRandom()}",
                            value = summary.resetLike().toJson(),
                        ),
                        DoorayAction.createButton(
                            name = LunchInteractionType.RESTART,
                            text = "처음으로 ${RESET_EMOJIS.getRandom()}",
                            value = LunchActionSummary.createBy(summary.convertResponseType()).toJson()
                        ),
                    )
                )
            )
        )

        fun createLunchDetailForm(summary: LunchActionSummary) = LunchCommandResponse(
            text = "카테고리를 선택해주세요.",
            responseType = summary.responseType,
            attachments = listOf(
                DoorayAttachment(
                    actions = LunchItemType.entries.map { lunchType ->
                        DoorayAction.createButton(
                            name = LunchInteractionType.GET_RECOMMENDATION,
                            text = "${lunchType.label} ${lunchType.getEmoji()}",
                            value = summary.changeItemType(lunchType).toJson(),
                        )
                    }
                ),
            ),
        )

        fun createLunchConfirmResult(item: LunchItem, summary: LunchActionSummary): LunchCommandResponse {
            return if (summary.isInChannel()) {
                createPublicLunchConfirmResult(item)
            } else createPrivateLunchConfirmResult(item)
        }

        //혼자 고르기 할때 공유하기
        private fun createPrivateLunchConfirmResult(item: LunchItem) = LunchCommandResponse(
            text = "오늘 점심으로 `${item.name}`(${item.type.label}) 어떠세요? 🤔",
            responseType = IN_CHANNEL.value,
            deleteOriginal = true,
            attachments = listOf(
                DoorayAttachment(
                    title = "${item.name} - 메뉴 보러가기",
                    titleLink = item.link,
                ),
            )
        )

        //같이 고르기 할때 확정하기
        private fun createPublicLunchConfirmResult(item: LunchItem) = LunchCommandResponse(
            text = CONFIRM_LIST.shuffled().first(),
            responseType = IN_CHANNEL.value,
            deleteOriginal = true,
            attachments = listOf(
                DoorayAttachment(
                    title = "${item.name} - 메뉴 보러가기",
                    titleLink = item.link,
                ),
            )
        )

        fun createCancel(summary: LunchActionSummary) = LunchCommandResponse(
            text = "다음에 다시 만나요 ${RESET_EMOJIS.getRandom()}",
            responseType = summary.responseType,
            deleteOriginal = true,
        )

        fun createHelp(summary: LunchActionSummary) = LunchCommandResponse(
            text = "이용해 주셔서 감사합니다 ${HAPPY_EMOJIS.getRandom()}",
            responseType = summary.responseType,
            attachments = listOf(
                DoorayAttachment(
                    title = "식당 전체 목록 보러가기 📍",
                    titleLink = "https://naver.me/xRhSJQca",
                ),
                DoorayAttachment(
                    title = "신규 식당 추가 또는 피드백 전송 💭",
                    titleLink = "https://forms.gle/Ewrzmg7dJiZxBeeJA",
                )
            )
        )
    }
}
