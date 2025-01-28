package dev.weekend.slashcommand.infrastructure.config

import io.netty.channel.ChannelOption.CONNECT_TIMEOUT_MILLIS
import io.netty.channel.epoll.EpollChannelOption.TCP_KEEPIDLE
import io.netty.handler.timeout.ReadTimeoutHandler
import io.netty.handler.timeout.WriteTimeoutHandler
import org.springframework.context.annotation.Bean
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import reactor.netty.http.client.HttpClient
import reactor.netty.tcp.TcpClient
import java.util.concurrent.TimeUnit.MILLISECONDS

/**
 * @author Jaeguk Cho
 */

@Component
class WebClientConfig {
    @Bean
    fun getWebClient(): WebClient {
        val tcpClient = TcpClient.create()
            .option(CONNECT_TIMEOUT_MILLIS, 4000)
            .option(TCP_KEEPIDLE, KEEP_IDLE_SECONDS * 2)
            .doOnConnected { connection ->
                connection.addHandlerLast(ReadTimeoutHandler(4000, MILLISECONDS))
                    .addHandlerLast(WriteTimeoutHandler(4000, MILLISECONDS))
            }

        return WebClient.builder()
            .clientConnector(ReactorClientHttpConnector(HttpClient.from(tcpClient)))
            .build()
    }

    companion object {
        private const val KEEP_IDLE_SECONDS = 4
    }
}
