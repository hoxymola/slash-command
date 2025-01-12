package dev.weekend.slashcommand.presentation.model

/**
 * @author Jaeguk Cho
 */

data class VoteCreateRequest(
    val tenantId: String,
    val userId: String,
    val responseUrl: String,
)
