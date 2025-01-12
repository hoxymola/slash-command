package dev.weekend.slashcommand.presentation

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.web.reactive.function.server.coRouter

/**
 * @author Jaeguk Cho
 */

@Configuration
class CommandRouter(
    private val commandHandler: CommandHandler,
) {
    @Bean
    fun coCommandRouter() = coRouter {
        (accept(APPLICATION_JSON) and "/slash-command").nest {
            POST("/blind-vote", commandHandler::createBlindVote)
            POST("/blind-vote/interact", commandHandler::interactBlindVote)
            POST("/mbti", commandHandler::testMbti)
            POST("/mbti/interact", commandHandler::interactMbti)
        }
    }
}
