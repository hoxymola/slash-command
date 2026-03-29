package dev.weekend.slashcommand.application.model

import dev.weekend.slashcommand.domain.enums.DoorayResponseType
import dev.weekend.slashcommand.domain.enums.DoorayResponseType.EPHEMERAL
import dev.weekend.slashcommand.domain.enums.DoorayResponseType.IN_CHANNEL
import dev.weekend.slashcommand.domain.enums.LunchItemType

/**
 * @author Yoohwa Cho
 */
data class LunchActionSummary(
    val historyNo: Long = 0,
    val responseType: DoorayResponseType = EPHEMERAL,
) {
    var itemNo: String = ""
    var itemType: String = ""
    var liked: String = ""

    fun changeItemNo(itemNo: Long): LunchActionSummary {
        return this.apply {
            this.itemNo = itemNo.toString()
        }
    }

    fun changeItemType(itemType: LunchItemType): LunchActionSummary {
        return this.apply {
            this.itemType = itemType.name
        }
    }

    fun likeItem(): LunchActionSummary {
        return this.apply {
            this.liked = "Y"
        }
    }

    fun dislikeItem(): LunchActionSummary {
        return this.apply {
            this.liked = "N"
        }
    }

    fun resetLike(): LunchActionSummary {
        return this.apply {
            this.liked = ""
        }
    }

    fun isInChannel(): Boolean {
        return responseType == IN_CHANNEL
    }

    fun convertResponseType(): DoorayResponseType {
        return if (responseType == IN_CHANNEL) IN_CHANNEL
        else EPHEMERAL
    }

    fun convertItemNo(): Long {
        return itemNo.toLongOrNull() ?: 0L
    }

    fun convertItemType(): LunchItemType {
        return LunchItemType.valueOf(itemType)
    }

    companion object {
        fun createBy(
            historyNo: Long,
            responseType: DoorayResponseType,
        ): LunchActionSummary = LunchActionSummary(
            historyNo = historyNo,
            responseType = responseType,
        )
    }
}
