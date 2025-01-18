package dev.weekend.slashcommand.domain.entity

import dev.weekend.slashcommand.domain.model.MbtiAnswer
import jakarta.persistence.*
import java.io.Serializable

/**
 * @author Jaeguk Cho
 */

@Entity
@Table(name = "mbti_test")
@IdClass(MbtiTestKey::class)
data class MbtiTest(
    @Id
    val testNo: Long,

    @Id
    val seq: Int,

    @ManyToOne(targetEntity = MbtiQuestion::class)
    @JoinColumn(name = "question_no")
    val question: MbtiQuestion,

    val userId: Long,

    @Embedded
    var answer: MbtiAnswer? = null,
) : BaseTimeEntity() {
    fun chooseFirstAnswer() {
        answer = question.firstChoice
    }

    fun chooseSecondAnswer() {
        answer = question.secondChoice
    }

    companion object {
        fun createBy(
            testNo: Long,
            question: MbtiQuestion,
            userId: Long,
        ) = MbtiTest(
            testNo = testNo,
            seq = question.seq,
            question = question,
            userId = userId,
        )
    }
}

data class MbtiTestKey(
    val testNo: Long = 0,
    val seq: Int = 0,
) : Serializable
