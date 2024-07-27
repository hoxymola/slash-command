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
@Table(name = "blind_vote_emoji")
data class BlindVoteEmoji(
    @Id
    @GeneratedValue(strategy = IDENTITY)
    val voteEmojiNo: Long = 0,

    val emoji: String,
) : BaseTimeEntity()
