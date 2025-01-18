package dev.weekend.slashcommand.application

import dev.weekend.slashcommand.domain.constant.MbtiConstant.FIRST_QUESTION_SEQ
import dev.weekend.slashcommand.domain.constant.MbtiConstant.MBTI_TYPE_COUNT
import dev.weekend.slashcommand.domain.entity.MbtiResult
import dev.weekend.slashcommand.domain.entity.MbtiTest
import dev.weekend.slashcommand.domain.entity.MbtiTestKey
import dev.weekend.slashcommand.domain.entity.MbtiTestMapping
import dev.weekend.slashcommand.domain.enums.DoorayResponseType.EPHEMERAL
import dev.weekend.slashcommand.domain.enums.DoorayResponseType.IN_CHANNEL
import dev.weekend.slashcommand.domain.enums.MbtiInteractionType.*
import dev.weekend.slashcommand.domain.enums.MbtiType
import dev.weekend.slashcommand.domain.repository.*
import dev.weekend.slashcommand.presentation.model.CommandResponse
import dev.weekend.slashcommand.presentation.model.MbtiInteractRequest
import dev.weekend.slashcommand.presentation.model.MbtiTestRequest
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.support.TransactionTemplate

/**
 * @author Jaeguk Cho
 */

@Service
class MbtiService(
    private val mbtiResultRepository: MbtiResultRepository,
    private val mbtiQuestionRepository: MbtiQuestionRepository,
    private val mbtiTestRepository: MbtiTestRepository,
    private val mbtiTestMappingRepository: MbtiTestMappingRepository,
    private val mbtiDetailRepository: MbtiDetailRepository,
    private val transactionTemplate: TransactionTemplate,
) {
    fun testMbti(
        request: MbtiTestRequest
    ): CommandResponse {
        val mbtiResult = mbtiResultRepository.findByUserId(request.userId)
        val mbtiDetail = mbtiResult?.let { mbtiDetailRepository.findByIdOrNull(it.mbti) }

        return CommandResponse.createFormBy(
            mbtiResult = mbtiResult,
            mbtiDetail = mbtiDetail,
        )
    }

    fun interactMbti(
        request: MbtiInteractRequest,
    ): CommandResponse {
        return when (request.actionName) {
            START_TEST -> request.startTest()
            CANCEL_TEST -> request.cancelTest()
            RESTART_TEST -> request.restartTest()
            FIRST_ANSWER -> request.firstAnswer()
            SECOND_ANSWER -> request.secondAnswer()
            PREV_QUESTION -> request.prevQuestion()
            NEXT_QUESTION -> request.nextQuestion()
            GET_RESULT -> request.getResult()
            SHARE_RESULT -> request.shareResult()
            GET_STATISTICS -> request.getStatistics()
            SHARE_STATISTICS -> request.shareStatistics()
        }
    }

    private fun MbtiInteractRequest.startTest(): CommandResponse {
        val question = mbtiQuestionRepository.getRandomQuestionBySeq(FIRST_QUESTION_SEQ)
        val testMapping = MbtiTestMapping.createBy(
            userId = userId,
        ).also { mbtiTestMappingRepository.save(it) }

        val test = MbtiTest.createBy(
            testNo = testMapping.testNo,
            userId = userId,
            question = question,
        ).let { mbtiTestRepository.save(it) }

        return CommandResponse.createQuestionBy(
            mbtiTest = test,
        )
    }

    private fun MbtiInteractRequest.cancelTest(): CommandResponse {
        return CommandResponse.createCancelTest()
    }

    private fun MbtiInteractRequest.restartTest(): CommandResponse {
        val mbtiResult = mbtiResultRepository.findByUserId(userId)
        val mbtiDetail = mbtiResult?.let { mbtiDetailRepository.findByIdOrNull(it.mbti) }

        return CommandResponse.createFormBy(
            mbtiResult = mbtiResult,
            mbtiDetail = mbtiDetail,
        )
    }

    private fun MbtiInteractRequest.firstAnswer(): CommandResponse {
        return transactionTemplate.execute {
            val test = mbtiTestRepository.findByIdOrNull(MbtiTestKey(testNo, seq)) ?: throw NotFoundException()

            test.chooseFirstAnswer()

            CommandResponse.createQuestionBy(
                mbtiTest = test,
            )
        } ?: CommandResponse.createResponse()
    }

    private fun MbtiInteractRequest.secondAnswer(): CommandResponse {
        return transactionTemplate.execute {
            val test = mbtiTestRepository.findByIdOrNull(MbtiTestKey(testNo, seq)) ?: throw NotFoundException()

            test.chooseSecondAnswer()

            CommandResponse.createQuestionBy(
                mbtiTest = test,
            )
        } ?: CommandResponse.createResponse()
    }

    private fun MbtiInteractRequest.prevQuestion(): CommandResponse {
        val test = mbtiTestRepository.findByIdOrNull(MbtiTestKey(testNo, seq - 1)) ?: throw NotFoundException()

        return CommandResponse.createQuestionBy(
            mbtiTest = test,
        )
    }

    private fun MbtiInteractRequest.nextQuestion(): CommandResponse {
        val currentTest = mbtiTestRepository.findByIdOrNull(MbtiTestKey(testNo, seq)) ?: throw NotFoundException()
        val nextTest = mbtiTestRepository.findByIdOrNull(MbtiTestKey(testNo, seq + 1))
        val test = nextTest ?: MbtiTest.createBy(
            testNo = currentTest.testNo,
            userId = userId,
            question = mbtiQuestionRepository.getRandomQuestionBySeq(seq + 1),
        ).let { mbtiTestRepository.save(it) }

        return CommandResponse.createQuestionBy(
            mbtiTest = test,
        )
    }

    private fun MbtiInteractRequest.getResult(): CommandResponse {
        return transactionTemplate.execute {
            val tests = mbtiTestRepository.findByTestNo(testNo)
            val (firstTrait, secondTrait, thirdTrait, fourthTrait) = tests.mapNotNull { it.answer?.trait }
            val mbti = MbtiType.getByTraits(
                firstTrait = firstTrait,
                secondTrait = secondTrait,
                thirdTrait = thirdTrait,
                fourthTrait = fourthTrait,
            )
            val mbtiDetail = mbtiDetailRepository.findByIdOrNull(mbti) ?: throw NotFoundException()
            val prevResult = mbtiResultRepository.findByUserId(userId)
            val result = prevResult?.apply { updateMbti(mbti) } ?: MbtiResult.createBy(
                userId = userId,
                mbti = mbti,
            ).let { mbtiResultRepository.save(it) }

            CommandResponse.createResultBy(
                mbtiResult = result,
                mbtiDetail = mbtiDetail,
                responseType = EPHEMERAL,
            )
        } ?: CommandResponse.createResponse()
    }

    private fun MbtiInteractRequest.shareResult(): CommandResponse {
        val result = mbtiResultRepository.findByUserId(userId) ?: throw NotFoundException()
        val detail = mbtiDetailRepository.findByIdOrNull(result.mbti) ?: throw NotFoundException()

        return CommandResponse.createResultBy(
            mbtiResult = result,
            mbtiDetail = detail,
            responseType = IN_CHANNEL,
            deleteOriginal = true,
            tenantId = tenant.id,
            userId = user.id,
        )
    }

    private fun MbtiInteractRequest.getStatistics(): CommandResponse {
        val results = mbtiResultRepository.findAll()
        val totalCount = results.size - MBTI_TYPE_COUNT

        return CommandResponse.createStatisticsBy(
            mbtiResults = results,
            totalCount = totalCount,
            responseType = EPHEMERAL,
        )
    }

    private fun MbtiInteractRequest.shareStatistics(): CommandResponse {
        val results = mbtiResultRepository.findAll()
        val totalCount = results.size - MBTI_TYPE_COUNT

        return CommandResponse.createStatisticsBy(
            mbtiResults = results,
            totalCount = totalCount,
            responseType = IN_CHANNEL,
            deleteOriginal = true,
        )
    }
}
