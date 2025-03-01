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

    @Column(name = "lunch_name")
    val name: String = "",

    @Column(name = "lunch_link")
    val link: String = "",

    @Enumerated(EnumType.STRING)
    @Column(name = "lunch_item_type")
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
