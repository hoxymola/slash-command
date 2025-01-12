package dev.weekend.slashcommand.domain.repository

import dev.weekend.slashcommand.domain.entity.MbtiQuestion
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

/**
 * @author Jaeguk Cho
 */

interface MbtiQuestionRepository : JpaRepository<MbtiQuestion, Long> {
    @Query(
        """
            select *
            from mbti_question
            where seq = :seq
            order by Rand()
            limit 1
        """,
        nativeQuery = true,
    )
    fun getRandomQuestionBySeq(
        seq: Int,
    ): MbtiQuestion
}
