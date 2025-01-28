package dev.weekend.slashcommand.application.model

import dev.weekend.slashcommand.domain.enums.LunchItemType

/**
 * @author Yoohwa Cho
 */
data class LunchItemCreateCommand(
    val name: String,
    val link: String,
    val type: LunchItemType,
)
