package dev.weekend.slashcommand.presentation

import dev.weekend.slashcommand.application.CommandService
import dev.weekend.slashcommand.domain.model.DoorayChannel
import dev.weekend.slashcommand.infrastructure.client.DoorayClient
import dev.weekend.slashcommand.presentation.model.FormCreateRequest
import dev.weekend.slashcommand.presentation.model.VoteUpdateRequest
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
    private val commandService: CommandService,
    private val doorayClient: DoorayClient,
) {
    suspend fun createBlindVoteForm(request: ServerRequest): ServerResponse {
        return withContext(Dispatchers.Default) {
            val createRequest = request.awaitBody<FormCreateRequest>()

            commandService.createBlindVote(createRequest)
                .let { ok().bodyValueAndAwait(it) }
        }
    }

    suspend fun updateBlindVote(request: ServerRequest): ServerResponse {
        return withContext(Dispatchers.Default) {
            val createRequest = request.awaitBody<VoteUpdateRequest>()

            commandService.updateBlindVote(createRequest)
                .let { ok().bodyValueAndAwait(it) }
        }
    }
}

data class Temp(
    val responseUrl: String,
    val channel: DoorayChannel,
)

data class Temp2(
    val errors: List<Any>,
)
