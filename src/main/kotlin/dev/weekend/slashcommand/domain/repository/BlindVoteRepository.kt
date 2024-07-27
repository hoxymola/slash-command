package dev.weekend.slashcommand.domain.repository

import dev.weekend.slashcommand.domain.entity.BlindVote
import org.springframework.data.jpa.repository.JpaRepository

/**
 * @author Jaeguk Cho
 */

interface BlindVoteRepository : JpaRepository<BlindVote, Long> {
}
