package dev.weekend.slashcommand.domain.entity

import jakarta.persistence.*
import jakarta.persistence.GenerationType.IDENTITY
import org.hibernate.type.YesNoConverter

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

    val userId: Long,

    val tenantId: Long,

    var responseUrl: String,
) : BaseTimeEntity() {
    var voteTitle: String? = null
        private set

    var voteLink: String? = null
        private set

    var selectableItemCnt: Int = 0
        private set

    @Convert(converter = YesNoConverter::class)
    @Column(name = "show_progress_yn")
    var showProgress: Boolean = true

    fun updateTitle(
        voteTitle: String,
    ) {
        this.voteTitle = voteTitle
    }

    fun updateLink(
        voteLink: String?,
    ) {
        this.voteLink = voteLink
    }

    fun updateSelectableItemCnt(
        selectableItemCnt: Int,
    ) {
        this.selectableItemCnt = selectableItemCnt
    }

    fun updateShowProgress(
        showProgress: Boolean,
    ) {
        this.showProgress = showProgress
    }

    fun updateResponseUrl(
        responseUrl: String,
    ) {
        this.responseUrl = responseUrl
    }

    companion object {
        fun createBy(
            emoji: BlindVoteEmoji,
            userId: String,
            tenantId: String,
            responseUrl: String,
        ) = BlindVote(
            voteEmoji = emoji,
            userId = userId.toLong(),
            tenantId = tenantId.toLong(),
            responseUrl = responseUrl,
        )
    }
}
