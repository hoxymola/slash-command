package dev.weekend.slashcommand.domain.repository

import dev.weekend.slashcommand.domain.entity.BlindVoteMember
import org.springframework.data.jpa.repository.JpaRepository

/**
 * @author Jaeguk Cho
 */

interface BlindVoteMemberRepository : JpaRepository<BlindVoteMember, Long> {
    fun findByVoteItemNoAndUserId(
        voteItemNo: Long,
        userId: Long,
    ): BlindVoteMember?

    fun countByVoteVoteNoAndUserId(
        voteNo: Long,
        userId: Long,
    ): Int
}
