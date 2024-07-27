package dev.weekend.slashcommand.domain.repository

import dev.weekend.slashcommand.domain.entity.BlindVoteItem
import org.springframework.data.jpa.repository.JpaRepository

/**
 * @author Jaeguk Cho
 */

interface BlindVoteItemRepository : JpaRepository<BlindVoteItem, Long> {
    fun findByVoteVoteNo(
        voteNo: Long,
    ): List<BlindVoteItem>
}
