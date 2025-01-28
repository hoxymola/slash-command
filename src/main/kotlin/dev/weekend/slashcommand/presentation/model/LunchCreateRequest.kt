package dev.weekend.slashcommand.presentation.model

import dev.weekend.slashcommand.application.model.LunchItemCreateCommand

/**
 * @author Yoohwa Cho
 */
data class LunchCreateRequest(
    val lists: List<LunchItemCreateCommand>,
)
