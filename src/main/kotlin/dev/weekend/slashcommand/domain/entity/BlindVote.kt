package dev.weekend.slashcommand.domain.entity

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType.IDENTITY
import jakarta.persistence.Id
import jakarta.persistence.Table

/**
 * @author Jaeguk Cho
 */

@Entity
@Table(name = "blind_vote")
data class BlindVote(
    @Id
    @GeneratedValue(strategy = IDENTITY)
    val voteNo: Long = 0,

    var voteTitle: String,

    var selectableItemCnt: Int,

    val userId: Long,

    val tenantId: Long,
) : BaseTimeEntity() {
    fun updateTitle(
        voteTitle: String,
    ) {
        this.voteTitle = voteTitle
    }

    fun updateSelectableItemCnt(
        selectableItemCnt: Int,
    ) {
        this.selectableItemCnt = selectableItemCnt
    }

    companion object {
        fun createBy(
            voteTitle: String = "",
            selectableItemCnt: Int = 0,
            userId: String,
            tenantId: String,
        ) = BlindVote(
            voteTitle = voteTitle,
            selectableItemCnt = selectableItemCnt,
            userId = userId.toLong(),
            tenantId = tenantId.toLong(),
        )
    }
}
