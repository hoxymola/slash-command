package dev.weekend.slashcommand.domain.repository

import dev.weekend.slashcommand.domain.entity.LunchItem
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

/**
 * @author Yoohwa Cho
 */
interface LunchItemRepository : JpaRepository<LunchItem, Long> {
    @Query(
        """
        select *
        from lunch_item
        order by Rand()
        limit 1
        """,
        nativeQuery = true,
    )
    fun getRandomItem(): LunchItem

    @Query(
        """
        select * 
        from lunch_item 
        where lunch_item_type = :type
        order by rand()
        limit 1
        """,
        nativeQuery = true,
    )
    fun getRandomItemByType(type: String): LunchItem
}
