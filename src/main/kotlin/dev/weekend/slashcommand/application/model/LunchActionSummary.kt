package dev.weekend.slashcommand.application.model

import dev.weekend.slashcommand.domain.enums.DoorayResponseType
import dev.weekend.slashcommand.domain.enums.LunchItemType

/**
 * @author Yoohwa Cho
 */
data class LunchActionSummary(
    val responseType: String = "",
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
        return responseType == DoorayResponseType.IN_CHANNEL.value
    }

    fun convertResponseType(): DoorayResponseType {
        return if (responseType == DoorayResponseType.IN_CHANNEL.value) DoorayResponseType.IN_CHANNEL
        else DoorayResponseType.EPHEMERAL
    }

    fun convertItemNo(): Long {
        return itemNo.toLongOrNull() ?: 0L
    }

    fun convertItemType(): LunchItemType {
        return LunchItemType.valueOf(itemType)
    }

    companion object {
        fun createBy(responseType: DoorayResponseType): LunchActionSummary = LunchActionSummary(
            responseType = responseType.value,
        )
    }
}
