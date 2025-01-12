package dev.weekend.slashcommand.domain.entity

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType.IDENTITY
import jakarta.persistence.Id
import jakarta.persistence.Table

/**
 * @author Jaeguk Cho
 */

@Entity
@Table(name = "mbti_test_mapping")
data class MbtiTestMapping(
    @Id
    @GeneratedValue(strategy = IDENTITY)
    val testNo: Long = 0,

    val userId: Long,
) {
    companion object {
        fun createBy(
            userId: Long,
        ) = MbtiTestMapping(
            userId = userId,
        )
    }
}
