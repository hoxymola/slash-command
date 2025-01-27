package dev.weekend.slashcommand.application

import dev.weekend.slashcommand.domain.entity.LunchItem
import dev.weekend.slashcommand.domain.enums.LunchInteractionType
import dev.weekend.slashcommand.domain.repository.LunchItemRepository
import dev.weekend.slashcommand.presentation.model.CommandResponse
import dev.weekend.slashcommand.presentation.model.LunchCreateRequest
import dev.weekend.slashcommand.presentation.model.LunchInteractRequest
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
    fun start(
        request: LunchStartRequest,
    ): CommandResponse {

        return CommandResponse.createLunchFormBy()
    }

    fun interact(request: LunchInteractRequest, og: String): CommandResponse {
        return when(request.actionName) {
            LunchInteractionType.GET_RECOMMENDATION -> request.getRecommendation(og) //추천 받기 - 전체 랜덤
            LunchInteractionType.START_DETAIL_RECOMMEND -> request.startDetailRecommendation(og) //타입에 따라 추천 받기
            else -> CommandResponse.createResponse(
                text = "oops,,, 아직 제공하지 않는 기능입니다."
            )
            //TODO interaction 추가하기
//            LunchInteractionType.RECOMMEND_AGAIN, //추천 다시 받기
//            LunchInteractionType.CONFIRM_RECOMMEND, //추천 확정하기
//            LunchInteractionType.CANCEL_RECOMMEND, //추천 취소하기
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

    private fun LunchInteractRequest.getRecommendation(og: String): CommandResponse {
        val item = lunchItemRepository.getRandomItem()

        return CommandResponse.createLunchResultBy(item, og)
    }

    private fun LunchInteractRequest.startDetailRecommendation(og: String): CommandResponse {
        return CommandResponse.createLunchDetailForm(og)
    }
}