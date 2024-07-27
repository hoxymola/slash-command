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

    val channelId: Long,
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
            voteTitle: String,
            selectableItemCnt: Int,
            userId: String,
            channelId: String,
        ) = BlindVote(
            voteTitle = voteTitle,
            selectableItemCnt = selectableItemCnt,
            userId = userId.toLong(),
            channelId = channelId.toLong(),
        )
    }
}
