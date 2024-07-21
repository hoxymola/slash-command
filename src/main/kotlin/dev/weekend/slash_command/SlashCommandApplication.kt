package dev.weekend.slash_command

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class SlashCommandApplication

fun main(args: Array<String>) {
	runApplication<SlashCommandApplication>(*args)
}
