package dev.weekend.slashcommand.domain.repository

import dev.weekend.slashcommand.domain.entity.MbtiResult
import org.springframework.data.jpa.repository.JpaRepository

/**
 * @author Jaeguk Cho
 */

interface MbtiResultRepository : JpaRepository<MbtiResult, Long> {
    fun findByUserId(userId: Long): MbtiResult?
}
