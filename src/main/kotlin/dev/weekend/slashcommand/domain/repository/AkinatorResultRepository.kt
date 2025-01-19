package dev.weekend.slashcommand.domain.repository

import dev.weekend.slashcommand.domain.entity.AkinatorResult
import org.springframework.data.jpa.repository.JpaRepository

/**
 * @author Jaeguk Cho
 */

interface AkinatorResultRepository : JpaRepository<AkinatorResult, Long> {
    fun findTopByUserIdOrderByAkinatorNoDesc(userId: String): AkinatorResult?
}
