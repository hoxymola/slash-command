package dev.weekend.slashcommand.domain.entity

import dev.weekend.slashcommand.domain.enums.MbtiType
import jakarta.persistence.Entity
import jakarta.persistence.EnumType.STRING
import jakarta.persistence.Enumerated
import jakarta.persistence.Id
import jakarta.persistence.Table

/**
 * @author Jaeguk Cho
 */

@Entity
@Table(name = "mbti_detail")
data class MbtiDetail(
    @Id
    @Enumerated(STRING)
    val mbti: MbtiType,

    val imageUrl: String,

    val url: String,

    val alias: String,
)
