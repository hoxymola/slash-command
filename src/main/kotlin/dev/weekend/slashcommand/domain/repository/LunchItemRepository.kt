package dev.weekend.slashcommand.domain.repository

import dev.weekend.slashcommand.domain.entity.LunchItem
import org.springframework.data.jpa.repository.JpaRepository

/**
 * @author Yoohwa Cho
 */
interface LunchItemRepository : JpaRepository<LunchItem, Long> {}