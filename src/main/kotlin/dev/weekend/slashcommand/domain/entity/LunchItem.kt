package dev.weekend.slashcommand.domain.entity

import dev.weekend.slashcommand.domain.enums.LunchItemType
import jakarta.persistence.*

/**
 * @author Yoohwa Cho
 */
@Entity
@Table(name = "lunch_item")
data class LunchItem(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "lunch_item_no")
    val no: Long = 0,

    val name: String = "",

    val link: String = "",

    @Enumerated(EnumType.STRING)
    val type: LunchItemType = LunchItemType.ETC,
) : BaseTimeEntity() {
    var likeCount: Int = 0
        private set

    var dislikeCount: Int = 0
        private set

    companion object {
        fun createBy(
            name: String,
            link: String,
            type: LunchItemType = LunchItemType.ETC,
        ) = LunchItem(
            name = name,
            link = link,
            type = type,
        )
    }
}
