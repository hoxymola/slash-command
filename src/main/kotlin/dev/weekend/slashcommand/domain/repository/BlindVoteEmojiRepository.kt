package dev.weekend.slashcommand.domain.repository

import dev.weekend.slashcommand.domain.entity.BlindVoteEmoji
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

/**
 * @author Jaeguk Cho
 */

interface BlindVoteEmojiRepository : JpaRepository<BlindVoteEmoji, Long> {
    @Query(
        """
            select *
            from blind_vote_emoji
            order by Rand()
            limit 1
        """,
        nativeQuery = true,
    )
    fun getRandomEmoji(): BlindVoteEmoji
}
