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

    val voteItemNo: Long,

    val userId: Long,
) : BaseTimeEntity() {
    companion object {
        fun createBy(
            vote: BlindVote,
            voteItemNo: Long,
            userId: Long,
        ) = BlindVoteMember(
            vote = vote,
            voteItemNo = voteItemNo,
            userId = userId,
        )
    }
}
