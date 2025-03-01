package dev.weekend.slashcommand.application

import dev.weekend.slashcommand.domain.entity.LunchItem
import dev.weekend.slashcommand.domain.entity.LunchRecommendHistory
import dev.weekend.slashcommand.domain.enums.LunchInteractionType
import dev.weekend.slashcommand.domain.repository.LunchItemRepository
import dev.weekend.slashcommand.domain.repository.LunchRecommendHistoryRepository
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
    private val lunchRecommendHistoryRepository: LunchRecommendHistoryRepository,
    private val transactionTemplate: TransactionTemplate,
) {
    fun start(
        request: LunchStartRequest,
    ): LunchCommandResponse {
        val history = LunchRecommendHistory.createBy(
            userId = request.userId,
        ).also { lunchRecommendHistoryRepository.save(it) }

        return LunchCommandResponse.createLunchStartFormBy(history.no)
    }

    fun interact(request: LunchInteractRequest): LunchCommandResponse {
        return when (request.actionName) {
            LunchInteractionType.START,
            LunchInteractionType.RESTART -> request.startRecommendation() //시작하기
            LunchInteractionType.GET_RECOMMENDATION,
            LunchInteractionType.RECOMMEND_AGAIN -> request.getRecommendation() //추천 받기
            LunchInteractionType.START_DETAIL_RECOMMEND -> request.startDetailRecommendation() //타입에 따라 추천 받기
            LunchInteractionType.CONFIRM_RECOMMEND -> request.confirm() //추천 확정하기
            LunchInteractionType.HELP -> request.help()
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
        val item = if (summary.itemType.isNotBlank()) {
            lunchItemRepository.getRandomItemByType(summary.convertItemType().name)
        } else lunchItemRepository.getRandomItem()

        return LunchCommandResponse.createLunchResultBy(item, summary)
    }

    private fun LunchInteractRequest.startDetailRecommendation(): LunchCommandResponse {
        return LunchCommandResponse.createLunchDetailForm(summary)
    }

    private fun LunchInteractRequest.confirm(): LunchCommandResponse {
        return transactionTemplate.execute {
            val item = lunchItemRepository.findByIdOrNull(summary.convertItemNo()) ?: throw InternalError()

            lunchRecommendHistoryRepository.findByIdOrNull(summary.historyNo)
                ?.apply { updateLunchItem(item) }

            LunchCommandResponse.createLunchConfirmResult(item, summary)
        } ?: throw IllegalStateException()
    }

    private fun LunchInteractRequest.startRecommendation(): LunchCommandResponse {
        return transactionTemplate.execute {
            lunchRecommendHistoryRepository.findByIdOrNull(summary.historyNo)
                ?.apply { updateLunchSelectType(summary.responseType) }

            LunchCommandResponse.createLunchFormBy(summary)
        } ?: throw IllegalStateException()
    }

    private fun LunchInteractRequest.cancel(): LunchCommandResponse {
        return LunchCommandResponse.createCancel(summary)
    }

    private fun LunchInteractRequest.help(): LunchCommandResponse {
        return LunchCommandResponse.createHelp(summary)
    }
}
