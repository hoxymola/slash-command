package dev.weekend.slashcommand.presentation

import dev.weekend.slashcommand.domain.enums.DoorayButtonStyle.DEFAULT
import dev.weekend.slashcommand.domain.enums.DoorayButtonStyle.PRIMARY
import dev.weekend.slashcommand.domain.enums.DoorayResponseType.IN_CHANNEL
import dev.weekend.slashcommand.domain.model.DoorayAction
import dev.weekend.slashcommand.domain.model.DoorayAttachment
import dev.weekend.slashcommand.domain.model.DoorayField
import dev.weekend.slashcommand.presentation.model.BlindVoteCreateRequest
import dev.weekend.slashcommand.presentation.model.BlindVoteCreateResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.ServerResponse.ok
import org.springframework.web.reactive.function.server.awaitBody
import org.springframework.web.reactive.function.server.bodyValueAndAwait

/**
 * @author Jaeguk Cho
 */

@Component
class CommandHandler(
) {
    suspend fun createBlindVote(request: ServerRequest): ServerResponse {
        return withContext(Dispatchers.Default) {
            val createRequest = request.awaitBody<BlindVoteCreateRequest>()

            val createResponse = BlindVoteCreateResponse(
                text = "무기명 투표 생성",
                responseType = IN_CHANNEL.value,
                attachments = listOf(
                    DoorayAttachment(
                        callbackId = "create-blind-vote",
                        title = "투표 제목",
                        text = "이곳에 제목을 입력받을 수 있게 ..",
                    ),
                    DoorayAttachment(
                        fields = listOf(
                            DoorayField(
                                title = "항목",
                                value = "이곳에 항목을 추가할 수 있게 .."
                            ),
                        ),
                    ),
                    DoorayAttachment(
                        fields = listOf(
                            DoorayField(
                                title = "선택 가능한 개수",
                                value = "이곳에 몇명 선택할 수 있는지를 고를 수 있게 ..",
                            ),
                        ),
                    ),
                    DoorayAttachment(
                        callbackId = "send-button",
                        actions = listOf(
                            DoorayAction(
                                name = "send",
                                type = "button",
                                text = "생성",
                                value = "create",
                                style = PRIMARY.value,
                            ),
                            DoorayAction(
                                name = "send",
                                type = "button",
                                text = "취소",
                                value = "cancel",
                                style = DEFAULT.value,
                            )
                        )
                    )
                )
            )

            ok().bodyValueAndAwait(createResponse)
        }
    }
}
