package dev.weekend.slashcommand.domain.entity

import jakarta.persistence.*
import jakarta.persistence.GenerationType.IDENTITY

/**
 * @author Jaeguk Cho
 */

@Entity
@Table(name = "blind_vote_item")
data class BlindVoteItem(
    @Id
    @GeneratedValue(strategy = IDENTITY)
    val voteItemNo: Long = 0,

    @ManyToOne(targetEntity = BlindVote::class)
    @JoinColumn(name = "vote_no")
    val vote: BlindVote,

    var voteItemName: String,

    var voteCnt: Int = 0,
) : BaseTimeEntity() {
    fun updateName(
        voteItemName: String,
    ) {
        this.voteItemName = voteItemName
    }

    fun increaseCnt() {
        voteCnt++
    }

    fun decreaseCnt() {
        voteCnt--
    }

    companion object {
        fun createBy(
            vote: BlindVote,
            voteItemName: String,
        ) = BlindVoteItem(
            vote = vote,
            voteItemName = voteItemName,
        )
    }
}
