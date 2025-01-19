package dev.weekend.slashcommand.application

import dev.weekend.slashcommand.domain.entity.AkinatorResult
import dev.weekend.slashcommand.domain.enums.AkinatorInteractionType.*
import dev.weekend.slashcommand.domain.enums.DoorayResponseType.EPHEMERAL
import dev.weekend.slashcommand.domain.enums.DoorayResponseType.IN_CHANNEL
import dev.weekend.slashcommand.domain.repository.AkinatorResultRepository
import dev.weekend.slashcommand.infrastructure.cache.AkinatorCache
import dev.weekend.slashcommand.presentation.model.AkinatorInteractRequest
import dev.weekend.slashcommand.presentation.model.AkinatorStartRequest
import dev.weekend.slashcommand.presentation.model.CommandResponse
import org.eu.zajc.akiwrapper.Akiwrapper.Answer
import org.eu.zajc.akiwrapper.core.entities.impl.GuessImpl
import org.eu.zajc.akiwrapper.core.entities.impl.QuestionImpl
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.support.TransactionTemplate

/**
 * @author Jaeguk Cho
 */

@Service
class AkinatorService(
    private val akinatorCache: AkinatorCache,
    private val akinatorResultRepository: AkinatorResultRepository,
    private val transactionTemplate: TransactionTemplate,
) {
    fun startAkinator(
        request: AkinatorStartRequest,
    ): CommandResponse {
        val akinatorExists = akinatorCache.akinatorExists(request.userId)

        return CommandResponse.createFormBy(
            akinatorExists = akinatorExists,
        )
    }

    fun interactAkinator(
        request: AkinatorInteractRequest,
    ): CommandResponse {
        return when (request.actionName) {
            START_AKINATOR -> request.queryNewAkinator()
            CONTINUE_AKINATOR -> request.queryAkinator()
            CANCEL_AKINATOR -> request.cancelAkinator()
            ANSWER_QUESTION -> request.answerQuestion()
            UNDO_ANSWER -> request.undoAnswer()
            CONFIRM_GUESS -> request.confirmGuess()
            REJECT_GUESS -> request.rejectGuess()
            SHARE_GUESS -> request.shareGuess()
        }
    }

    private fun AkinatorInteractRequest.queryNewAkinator(): CommandResponse {
        AkinatorResult.createBy(
            userId = user.id,
        ).also { akinatorResultRepository.save(it) }

        akinatorCache.deleteAkinator(user.id)

        return queryAkinator()
    }

    private fun AkinatorInteractRequest.queryAkinator(): CommandResponse {
        val akinator = akinatorCache.getAkinator(user.id)

        return when (val query = akinator.currentQuery) {
            is QuestionImpl -> CommandResponse.createQuestionBy(query)
            is GuessImpl -> CommandResponse.createGuessBy(query)
            null -> CommandResponse.createResponse("아키네이터의 질문이 바닥났습니다. 🥺")
            else -> throw InternalError()
        }
    }

    private fun AkinatorInteractRequest.cancelAkinator(): CommandResponse {
        return CommandResponse.createCancelAkinator()
    }

    private fun AkinatorInteractRequest.answerQuestion(): CommandResponse {
        akinatorCache.getAkinator(user.id).currentQuery.also {
            (it as QuestionImpl).answer(Answer.valueOf(answer))
        }

        return queryAkinator()
    }

    private fun AkinatorInteractRequest.undoAnswer(): CommandResponse {
        akinatorCache.getAkinator(user.id).currentQuery.also {
            (it as QuestionImpl).undoAnswer()
        }

        return queryAkinator()
    }

    private fun AkinatorInteractRequest.confirmGuess(): CommandResponse {
        return transactionTemplate.execute {
            val akinatorResult = akinatorResultRepository.findTopByUserIdOrderByAkinatorNoDesc(user.id)
                ?: throw NotFoundException()
            val akinator = akinatorCache.getAkinator(user.id).currentQuery as GuessImpl

            akinatorResult.apply {
                updateResult(akinator)
            }
            // akinator.confirm() 왜 에러나는지 모르겠음
            akinatorCache.deleteAkinator(user.id)

            CommandResponse.createResultBy(
                akinatorResult = akinatorResult,
                responseType = EPHEMERAL,
            )
        } ?: CommandResponse.createResponse()
    }

    private fun AkinatorInteractRequest.rejectGuess(): CommandResponse {
        akinatorCache.getAkinator(user.id).currentQuery.also {
            (it as GuessImpl).reject()
        }

        return queryAkinator()
    }

    private fun AkinatorInteractRequest.shareGuess(): CommandResponse {
        val akinatorResult = akinatorResultRepository.findTopByUserIdOrderByAkinatorNoDesc(user.id)
            ?: throw NotFoundException()

        return CommandResponse.createResultBy(
            akinatorResult = akinatorResult,
            responseType = IN_CHANNEL,
        )
    }
}
