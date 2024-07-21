package dev.weekend.slashcommand.presentation

import dev.weekend.slashcommand.application.CommandService
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.ServerResponse.ok
import org.springframework.web.reactive.function.server.bodyValueAndAwait
import org.springframework.web.reactive.function.server.queryParamOrNull

/**
 * @author Jaeguk Cho
 */

@Component
class CommandHandler(
    private val commandService: CommandService,
) {
    suspend fun getCommand(request: ServerRequest): ServerResponse {
        val commandNo = request.queryParamOrNull("commandNo")!!.toLong()

        return commandService.getCommand(
            commandNo = commandNo,
        ).let { ok().bodyValueAndAwait(it) }
    }

    suspend fun addCommand(request: ServerRequest): ServerResponse {
        val commandName = request.queryParamOrNull("commandName")!!

        return commandService.addCommand(
            commandName = commandName,
        ).let { ok().bodyValueAndAwait(it) }
    }
}
