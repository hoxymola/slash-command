package dev.weekend.slashcommand.presentation.model

import dev.weekend.slashcommand.application.model.LunchActionSummary
import dev.weekend.slashcommand.domain.entity.LunchItem
import dev.weekend.slashcommand.domain.enums.DoorayButtonStyle.PRIMARY
import dev.weekend.slashcommand.domain.enums.DoorayResponseType.EPHEMERAL
import dev.weekend.slashcommand.domain.enums.DoorayResponseType.IN_CHANNEL
import dev.weekend.slashcommand.domain.enums.LunchInteractionType
import dev.weekend.slashcommand.domain.enums.LunchItemType
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
                            value = LunchActionSummary.createByResponseType(EPHEMERAL)
                        ),
                        DoorayAction.createButton(
                            name = LunchInteractionType.START,
                            text = "같이 고를래요 👥",
                            value = LunchActionSummary.createByResponseType(IN_CHANNEL),
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

        fun createLunchResultBy(item: LunchItem, summary: LunchActionSummary) = LunchCommandResponse(
            text = "오늘 점심으로 `${item.name}`(${item.type.label}) 어떠세요?",
            responseType = EPHEMERAL.value,
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
                            text = "맘에 들어요 😋",
                            value = LunchActionSummary.addItemNo(summary, item.no),
                        ),
                        DoorayAction.createButton(
                            name = LunchInteractionType.RECOMMEND_AGAIN,
                            text = "${if (summary.itemType.isNotBlank()) item.type.label + " " else ""}다시 뽑기 🤨",
                            value = summary.toJson(),
                        ),
                        DoorayAction.createButton(
                            name = LunchInteractionType.RESTART,
                            text = "처음으로 😵‍💫",
                            value = LunchActionSummary.createByResponseType(summary.convertResponseType())
                        ),
                    )
                )
            )
        )

        fun createLunchDetailForm(summary: LunchActionSummary) = LunchCommandResponse(
            text = "카테고리를 선택해주세요.",
            replaceOriginal = true,
            attachments = listOf(
                DoorayAttachment(
                    actions = LunchItemType.entries.map { lunchType ->
                        DoorayAction.createButton(
                            name = LunchInteractionType.GET_RECOMMENDATION,
                            text = lunchType.label,
                            value = LunchActionSummary.addItemType(summary, lunchType),
                        )
                    }
                ),
            ),
        )

        fun createLunchConfirmResult(item: LunchItem) = LunchCommandResponse(
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

        fun createCancel(summary: LunchActionSummary) = LunchCommandResponse(
            text = "다음에 다시 만나요 😵‍💫",
            responseType = summary.responseType,
            deleteOriginal = true,
        )
    }
}
