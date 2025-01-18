package dev.weekend.slashcommand.domain.model

import dev.weekend.slashcommand.domain.enums.MbtiTrait
import jakarta.persistence.Embeddable
import jakarta.persistence.EnumType.STRING
import jakarta.persistence.Enumerated

/**
 * @author Jaeguk Cho
 */

@Embeddable
data class MbtiAnswer(
    val answer: String,

    @Enumerated(STRING)
    val trait: MbtiTrait,
)
