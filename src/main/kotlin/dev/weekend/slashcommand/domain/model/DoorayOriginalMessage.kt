package dev.weekend.slashcommand.domain.model

import com.fasterxml.jackson.annotation.JsonProperty
import dev.weekend.slashcommand.domain.enums.DoorayResponseType

/**
 * @author Yoohwa Cho
 */
data class DoorayOriginalMessage(
    val responseType: String,
)