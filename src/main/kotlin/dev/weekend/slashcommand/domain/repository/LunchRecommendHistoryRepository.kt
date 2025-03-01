package dev.weekend.slashcommand.domain.repository

import dev.weekend.slashcommand.domain.entity.LunchRecommendHistory
import org.springframework.data.jpa.repository.JpaRepository

/**
 * @author Jaeguk Cho
 */

interface LunchRecommendHistoryRepository : JpaRepository<LunchRecommendHistory, Long> {
}
