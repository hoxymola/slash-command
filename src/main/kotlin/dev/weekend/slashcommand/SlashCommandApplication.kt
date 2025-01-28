package dev.weekend.slashcommand

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaAuditing

@SpringBootApplication
@EnableJpaAuditing
class SlashCommandApplication

fun main(args: Array<String>) {
    runApplication<SlashCommandApplication>(*args)
}
