package dev.weekend.slashcommand.presentation.model

/**
 * @author Jaeguk Cho
 */

data class FormCreateRequest(
    val tenantId: String,
    val userId: String,
    val cmdToken: String,
)
