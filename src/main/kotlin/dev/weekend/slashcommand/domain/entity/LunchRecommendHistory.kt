package dev.weekend.slashcommand.domain.entity

import dev.weekend.slashcommand.domain.enums.DoorayResponseType
import jakarta.persistence.*
import jakarta.persistence.EnumType.STRING
import jakarta.persistence.GenerationType.IDENTITY

/**
 * @author Jaeguk Cho
 */

@Entity
@Table(name = "lunch_recommend_history")
data class LunchRecommendHistory(
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "lunch_recommend_history_no")
    val no: Long = 0,

    val userId: Long,
) : BaseTimeEntity() {
    private var lunchItemNo: Long? = null

    private var lunchName: String? = null

    @Enumerated(STRING)
    private var lunchSelectType: DoorayResponseType? = null

    fun updateLunchItem(
        lunchItem: LunchItem,
    ) {
        this.lunchItemNo = lunchItem.no
        this.lunchName = lunchItem.name
    }

    fun updateLunchSelectType(
        lunchSelectType: DoorayResponseType,
    ) {
        this.lunchSelectType = lunchSelectType
    }

    companion object {
        fun createBy(
            userId: Long,
        ) = LunchRecommendHistory(
            userId = userId,
        )
    }
}
