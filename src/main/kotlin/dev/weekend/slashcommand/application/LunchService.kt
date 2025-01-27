package dev.weekend.slashcommand.application

import dev.weekend.slashcommand.domain.entity.LunchItem
import dev.weekend.slashcommand.domain.enums.DoorayResponseType
import dev.weekend.slashcommand.domain.model.DoorayAttachment
import dev.weekend.slashcommand.domain.repository.LunchItemRepository
import dev.weekend.slashcommand.presentation.model.CommandResponse
import dev.weekend.slashcommand.presentation.model.LunchCreateRequest
import dev.weekend.slashcommand.presentation.model.LunchStartRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.support.TransactionTemplate

/**
 * @author Yoohwa Cho
 */

@Service
class LunchService(
    private val lunchItemRepository: LunchItemRepository,
    private val transactionTemplate: TransactionTemplate,
) {
    fun startLunch(
        request: LunchStartRequest,
    ): CommandResponse {
        //TODO DB 연동하기
        val recommendedLunch = "https://map.naver.com/p/entry/place/1968803899?c=16.92,0,0,0,dh&placePath=/menu"

        return CommandResponse(
            text = "오늘 점심으로 `지로식당` 어떠세요?",
            responseType = DoorayResponseType.IN_CHANNEL.value,
            deleteOriginal = true,
            attachments = listOf(
                DoorayAttachment(
                    title = "지로식당 - 메뉴 보러가기",
                    titleLink = recommendedLunch,
                )
            )
        )
    }

    fun createLunchItems(
        request: LunchCreateRequest,
    ) {
        request.lists.map {
            LunchItem.createBy(
                name = it.name,
                link = it.link,
                type = it.type,
            )
        }.let { items ->
            transactionTemplate.execute {
                lunchItemRepository.saveAll(items)
            }
        }
    }
}