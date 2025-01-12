package dev.weekend.slashcommand.domain.repository

import dev.weekend.slashcommand.domain.entity.MbtiDetail
import dev.weekend.slashcommand.domain.enums.MbtiType
import org.springframework.data.jpa.repository.JpaRepository

/**
 * @author Jaeguk Cho
 */

interface MbtiDetailRepository : JpaRepository<MbtiDetail, MbtiType>
