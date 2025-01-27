package dev.weekend.slashcommand.presentation.model

import dev.weekend.slashcommand.application.model.LunchItemCreateCommand
import dev.weekend.slashcommand.domain.enums.LunchItemType

/**
 * @author Yoohwa Cho
 */
data class LunchCreateRequest(
    val lists: List<LunchItemCreateCommand>,
)