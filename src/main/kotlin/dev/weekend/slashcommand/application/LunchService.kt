package dev.weekend.slashcommand.application

import dev.weekend.slashcommand.domain.entity.LunchItem
import dev.weekend.slashcommand.domain.enums.DoorayResponseType
import dev.weekend.slashcommand.domain.enums.LunchInteractionType
import dev.weekend.slashcommand.domain.repository.LunchItemRepository
import dev.weekend.slashcommand.presentation.model.LunchCommandResponse
import dev.weekend.slashcommand.presentation.model.LunchCreateRequest
import dev.weekend.slashcommand.presentation.model.LunchInteractRequest
import dev.weekend.slashcommand.presentation.model.LunchStartRequest
import org.springframework.data.repository.findByIdOrNull
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
    fun start(
        request: LunchStartRequest,
    ): LunchCommandResponse {
        return LunchCommandResponse.createLunchStartFormBy()
    }

    fun interact(request: LunchInteractRequest): LunchCommandResponse {
        return when (request.actionName) {
            LunchInteractionType.START,
            LunchInteractionType.RESTART -> request.start() //시작하기
            LunchInteractionType.GET_RECOMMENDATION,
            LunchInteractionType.RECOMMEND_AGAIN -> request.getRecommendation() //추천 받기
            LunchInteractionType.START_DETAIL_RECOMMEND -> request.startDetailRecommendation() //타입에 따라 추천 받기
            LunchInteractionType.CONFIRM_RECOMMEND -> request.confirm() //추천 확정하기
            else -> request.cancel()
        }
    }

    fun createItems(
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

    private fun LunchInteractRequest.getRecommendation(): LunchCommandResponse {
        val item = if (actionValue.isNotBlank()) {
            lunchItemRepository.getRandomItemByType(actionValue)
        } else lunchItemRepository.getRandomItem()

        return LunchCommandResponse.createLunchResultBy(item, actionValue, originalMessage.responseType)
    }

    private fun LunchInteractRequest.startDetailRecommendation(): LunchCommandResponse {
        return LunchCommandResponse.createLunchDetailForm(originalMessage.responseType)
    }

    private fun LunchInteractRequest.confirm(): LunchCommandResponse {
        val item = lunchItemRepository.findByIdOrNull(actionValue.toLong()) ?: throw InternalError()
        return LunchCommandResponse.createLunchConfirmResult(item)
    }

    private fun LunchInteractRequest.start(): LunchCommandResponse {
        val responseType = if (actionValue == DoorayResponseType.IN_CHANNEL.value) {
            actionValue
        } else originalMessage.responseType
        val req = this.toString()
        return LunchCommandResponse.createLunchFormBy(responseType, req)
    }

    private fun LunchInteractRequest.cancel(): LunchCommandResponse {
        return LunchCommandResponse.createCancel(originalMessage.responseType)
    }
}