package dev.weekend.slashcommand.presentation

import dev.weekend.slashcommand.application.AkinatorService
import dev.weekend.slashcommand.application.BlindVoteService
import dev.weekend.slashcommand.application.LunchService
import dev.weekend.slashcommand.application.MbtiService
import dev.weekend.slashcommand.presentation.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.*
import org.springframework.web.reactive.function.server.ServerResponse.noContent
import org.springframework.web.reactive.function.server.ServerResponse.ok

/**
 * @author Jaeguk Cho
 */

@Component
class CommandHandler(
    private val blindVoteService: BlindVoteService,
    private val mbtiService: MbtiService,
    private val akinatorService: AkinatorService,
    private val lunchService: LunchService,
) {
    suspend fun createBlindVote(request: ServerRequest): ServerResponse {
        return withContext(Dispatchers.Default) {
            val createRequest = request.awaitBody<VoteCreateRequest>()

            blindVoteService.createBlindVote(createRequest)
                .let { ok().bodyValueAndAwait(it) }
        }
    }

    suspend fun interactBlindVote(request: ServerRequest): ServerResponse {
        return withContext(Dispatchers.Default) {
            val interactRequest = request.awaitBody<VoteInteractRequest>()

            blindVoteService.interactBlindVote(interactRequest)
                .let { ok().bodyValueAndAwait(it) }
        }
    }

    suspend fun testMbti(request: ServerRequest): ServerResponse {
        return withContext(Dispatchers.Default) {
            val testRequest = request.awaitBody<MbtiTestRequest>()

            mbtiService.testMbti(testRequest)
                .let { ok().bodyValueAndAwait(it) }
        }
    }

    suspend fun interactMbti(request: ServerRequest): ServerResponse {
        return withContext(Dispatchers.Default) {
            val interactRequest = request.awaitBody<MbtiInteractRequest>()

            mbtiService.interactMbti(interactRequest)
                .let { ok().bodyValueAndAwait(it) }
        }
    }

    suspend fun createAkinator(request: ServerRequest): ServerResponse {
        return withContext(Dispatchers.Default) {
            val startRequest = request.awaitBody<AkinatorStartRequest>()

            akinatorService.startAkinator(startRequest)
                .let { ok().bodyValueAndAwait(it) }
        }
    }

    suspend fun interactAkinator(request: ServerRequest): ServerResponse {
        return withContext(Dispatchers.Default) {
            val interactRequest = request.awaitBody<AkinatorInteractRequest>()

            akinatorService.interactAkinator(interactRequest)
                .let { ok().bodyValueAndAwait(it) }
        }
    }

    suspend fun recommendLunch(request: ServerRequest): ServerResponse {
        return withContext(Dispatchers.Default) {
            val recommendRequest = request.awaitBody<LunchStartRequest>()

            lunchService.startLunch(recommendRequest)
                .let { ok().bodyValueAndAwait(it) }
        }
    }

    suspend fun createLunchItem(request: ServerRequest): ServerResponse {
        return withContext(Dispatchers.Default) {
            val createRequest = request.awaitBody<LunchCreateRequest>()

            lunchService.createLunchItems(createRequest)
                .let { noContent().buildAndAwait() }
        }
    }
}
