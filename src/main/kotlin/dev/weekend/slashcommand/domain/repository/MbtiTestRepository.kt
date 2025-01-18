package dev.weekend.slashcommand.domain.repository

import dev.weekend.slashcommand.domain.entity.MbtiTest
import dev.weekend.slashcommand.domain.entity.MbtiTestKey
import org.springframework.data.jpa.repository.JpaRepository

/**
 * @author Jaeguk Cho
 */

interface MbtiTestRepository : JpaRepository<MbtiTest, MbtiTestKey> {
    fun findByTestNo(testNo: Long): List<MbtiTest>
}
