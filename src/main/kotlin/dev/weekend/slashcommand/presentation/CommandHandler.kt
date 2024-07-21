package dev.weekend.slashcommand.presentation

import dev.weekend.slashcommand.domain.enums.ResponseType.EPHEMERAL
import dev.weekend.slashcommand.presentation.model.CommandResponse
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
    suspend fun hi(request: ServerRequest): ServerResponse {
        return withContext(Dispatchers.Default) {
            val hiRequest = request.awaitBody<CommandHandler>()
            val hiResponse = CommandResponse(
                text = "Hello, World!",
                responseType = EPHEMERAL.value,
            )

            ok().bodyValueAndAwait(hiResponse)
        }
    }
}
