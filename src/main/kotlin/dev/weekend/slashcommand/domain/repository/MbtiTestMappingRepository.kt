package dev.weekend.slashcommand.domain.repository

import dev.weekend.slashcommand.domain.entity.MbtiTestMapping
import org.springframework.data.jpa.repository.JpaRepository

/**
 * @author Jaeguk Cho
 */

interface MbtiTestMappingRepository : JpaRepository<MbtiTestMapping, Long> {
    fun findTopByUserIdOrderByTestNoDesc(userId: Long): MbtiTestMapping?
}
