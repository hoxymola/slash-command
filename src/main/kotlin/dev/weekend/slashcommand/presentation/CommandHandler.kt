package dev.weekend.slashcommand.presentation

import dev.weekend.slashcommand.application.BlindVoteService
import dev.weekend.slashcommand.application.MbtiService
import dev.weekend.slashcommand.presentation.model.MbtiInteractRequest
import dev.weekend.slashcommand.presentation.model.MbtiTestRequest
import dev.weekend.slashcommand.presentation.model.VoteCreateRequest
import dev.weekend.slashcommand.presentation.model.VoteInteractRequest
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
    private val blindVoteService: BlindVoteService,
    private val mbtiService: MbtiService,
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
}
