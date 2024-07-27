package dev.weekend.slashcommand.domain.entity

import jakarta.persistence.*
import jakarta.persistence.GenerationType.IDENTITY

/**
 * @author Jaeguk Cho
 */

@Entity
@Table(name = "blind_vote")
data class BlindVote(
    @Id
    @GeneratedValue(strategy = IDENTITY)
    val voteNo: Long = 0,

    @ManyToOne(targetEntity = BlindVoteEmoji::class)
    @JoinColumn(name = "vote_emoji_no")
    val voteEmoji: BlindVoteEmoji,

    var voteTitle: String,

    var selectableItemCnt: Int,

    val userId: Long,

    val tenantId: Long,

    var responseUrl: String,
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

    fun updateResponseUrl(
        responseUrl: String,
    ) {
        this.responseUrl = responseUrl
    }

    companion object {
        fun createBy(
            voteTitle: String = "",
            emoji: BlindVoteEmoji,
            selectableItemCnt: Int = 0,
            userId: String,
            tenantId: String,
            responseUrl: String,
        ) = BlindVote(
            voteTitle = voteTitle,
            voteEmoji = emoji,
            selectableItemCnt = selectableItemCnt,
            userId = userId.toLong(),
            tenantId = tenantId.toLong(),
            responseUrl = responseUrl,
        )
    }
}
