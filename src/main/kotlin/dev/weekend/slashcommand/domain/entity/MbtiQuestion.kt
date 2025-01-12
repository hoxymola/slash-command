package dev.weekend.slashcommand.domain.entity

import dev.weekend.slashcommand.domain.enums.MbtiDimension
import dev.weekend.slashcommand.domain.model.MbtiAnswer
import jakarta.persistence.*
import jakarta.persistence.EnumType.STRING
import jakarta.persistence.GenerationType.IDENTITY

/**
 * @author Jaeguk Cho
 */

@Entity
@Table(name = "mbti_question")
data class MbtiQuestion(
    @Id
    @GeneratedValue(strategy = IDENTITY)
    val questionNo: Long = 0,

    val question: String,

    val seq: Int,

    @Enumerated(STRING)
    val mbtiDimension: MbtiDimension,

    @Embedded
    @AttributeOverrides(
        AttributeOverride(name = "answer", column = Column(name = "first_answer")),
        AttributeOverride(name = "trait", column = Column(name = "first_trait")),
    )
    val firstChoice: MbtiAnswer,

    @Embedded
    @AttributeOverrides(
        AttributeOverride(name = "answer", column = Column(name = "second_answer")),
        AttributeOverride(name = "trait", column = Column(name = "second_trait")),
    )
    val secondChoice: MbtiAnswer,
) : BaseTimeEntity()
