package dev.weekend.slashcommand.infrastructure.client

import dev.weekend.slashcommand.application.model.DialogRequest
import dev.weekend.slashcommand.presentation.model.CommandResponse
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody

/**
 * @author Jaeguk Cho
 */

@Component
class DoorayClient(
    private val webClient: WebClient,
) {
    suspend fun openDialog(
        tenantDomain: String,
        channelId: String,
        cmdToken: String,
        request: DialogRequest,
    ): Any {
        return webClient.post()
            .uri("https://$tenantDomain/messenger/api/channels/$channelId/dialogs")
            .header("token", cmdToken)
            .bodyValue(request)
            .retrieve()
            .awaitBody()
    }

    suspend fun sendHook(
        uri: String,
        body: CommandResponse,
    ): Any {
        return webClient.post()
            .uri(uri)
            .bodyValue(body)
            .retrieve()
            .awaitBody()
    }
}
