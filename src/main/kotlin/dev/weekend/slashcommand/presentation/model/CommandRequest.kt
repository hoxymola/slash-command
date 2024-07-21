package dev.weekend.slashcommand.presentation.model

/**
 * @author Jaeguk Cho
 */

data class CommandRequest(
    val tenantId: String,
    val tenantDomain: String,
    val channelId: String,
    val channelName: String,
    val userId: String,
    val command: String,
    val text: String,
    val responseUrl: String,
    val appToken: String,
    val cmdToken: String,
    val triggerId: String,
)
