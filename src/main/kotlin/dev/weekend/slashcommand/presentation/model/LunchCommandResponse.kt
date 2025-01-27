package dev.weekend.slashcommand.presentation.model

import dev.weekend.slashcommand.domain.entity.LunchItem
import dev.weekend.slashcommand.domain.enums.DoorayButtonStyle.PRIMARY
import dev.weekend.slashcommand.domain.enums.DoorayResponseType.EPHEMERAL
import dev.weekend.slashcommand.domain.enums.DoorayResponseType.IN_CHANNEL
import dev.weekend.slashcommand.domain.enums.LunchInteractionType
import dev.weekend.slashcommand.domain.enums.LunchItemType
import dev.weekend.slashcommand.domain.model.DoorayAction
import dev.weekend.slashcommand.domain.model.DoorayAttachment

/**
 * @author Yoohwa Cho
 */
data class LunchCommandResponse(
    val text: String,
    val responseType: String,
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
                        ),
                        DoorayAction.createButton(
                            name = LunchInteractionType.START,
                            text = "같이 고를래요 👥",
                            value = IN_CHANNEL.value,
                        ),
                    )
                )
            )
        )

        fun createLunchFormBy(responseType: String, req: String) = LunchCommandResponse(
            text = "오늘의 점심 메뉴는? 😮",
            responseType = responseType,
            attachments = listOf(
                DoorayAttachment(
                    actions = listOfNotNull(
                        DoorayAction.createButton(
                            name = LunchInteractionType.GET_RECOMMENDATION,
                            text = "랜덤으로 추천받기",
                            style = PRIMARY,
                        ),
                        DoorayAction.createButton(
                            name = LunchInteractionType.START_DETAIL_RECOMMEND,
                            text = "카테고리 선택하기",
                        ),
                        DoorayAction.createButton(
                            name = LunchInteractionType.CANCEL,
                            text = "취소하기",
                        ),
                    )
                ), DoorayAttachment(
                    text = req
                )
            )
        )

        fun createLunchResultBy(item: LunchItem, value: String, responseType: String) = LunchCommandResponse(
            text = "오늘 점심으로 `${item.name}`(${item.type.label}) 어떠세요?",
            responseType = responseType,
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
                            value = item.no.toString(),
                        ),
                        DoorayAction.createButton(
                            name = LunchInteractionType.RECOMMEND_AGAIN,
                            text = "${if(value.isNotBlank()) item.type.label+" " else ""}다시 뽑기 🤨",
                            value = value,
                        ),
                        DoorayAction.createButton(
                            name = LunchInteractionType.RESTART,
                            text = "처음으로 😵‍💫",
                        ),
                    )
                )
            )
        )

        fun createLunchDetailForm(responseType: String) = LunchCommandResponse(
            text = "카테고리를 선택해주세요.",
            responseType = responseType,
            attachments = listOf(
                DoorayAttachment(
                    actions = LunchItemType.entries.map { lunchType ->
                        DoorayAction.createButton(
                            name = LunchInteractionType.GET_RECOMMENDATION,
                            text = lunchType.label,
                            value = lunchType.name,
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

        fun createCancel(responseType: String) = LunchCommandResponse(
            text = "다음에 다시 만나요 😵‍💫",
            responseType = responseType,
            deleteOriginal = true,
        )
    }
}
