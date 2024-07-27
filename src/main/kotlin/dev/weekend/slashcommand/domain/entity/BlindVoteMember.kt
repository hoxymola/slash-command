package dev.weekend.slashcommand.domain.entity

import jakarta.persistence.*
import jakarta.persistence.GenerationType.IDENTITY

/**
 * @author Jaeguk Cho
 */

@Entity
@Table(name = "blind_vote_member")
data class BlindVoteMember(
    @Id
    @GeneratedValue(strategy = IDENTITY)
    val voteMemberNo: Long = 0,

    @ManyToOne(targetEntity = BlindVote::class)
    @JoinColumn(name = "vote_no")
    val vote: BlindVote,

    @OneToOne(targetEntity = BlindVoteItem::class)
    @JoinColumn(name = "vote_item_no")
    val voteItem: BlindVoteItem,

    val userId: Long,
) : BaseTimeEntity() {
    companion object {
        fun createBy(
            vote: BlindVote,
            voteItem: BlindVoteItem,
            userId: Long,
        ) = BlindVoteMember(
            vote = vote,
            voteItem = voteItem,
            userId = userId,
        )
    }
}
