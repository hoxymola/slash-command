package dev.weekend.slashcommand.domain.entity

import dev.weekend.slashcommand.domain.enums.MbtiType
import jakarta.persistence.*
import jakarta.persistence.EnumType.STRING

/**
 * @author Jaeguk Cho
 */

@Entity
@Table(name = "mbti_result")
data class MbtiResult(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val resultNo: Long = 0,

    val userId: Long,

    @Enumerated(STRING)
    var mbti: MbtiType,
) : BaseTimeEntity() {
    fun updateMbti(
        mbti: MbtiType,
    ) {
        this.mbti = mbti
    }

    companion object {
        fun createBy(
            userId: Long,
            mbti: MbtiType,
        ) = MbtiResult(
            userId = userId,
            mbti = mbti,
        )
    }
}
