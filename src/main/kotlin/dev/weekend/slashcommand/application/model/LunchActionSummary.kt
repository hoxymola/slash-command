package dev.weekend.slashcommand.application.model

import dev.weekend.slashcommand.domain.enums.DoorayResponseType
import dev.weekend.slashcommand.domain.enums.LunchItemType
import dev.weekend.slashcommand.domain.extension.toJson

/**
 * @author Yoohwa Cho
 */
data class LunchActionSummary(
    val responseType: String = "",
    val itemNo: String = "",
    val itemType: String = "",
) {
    fun convertResponseType(): DoorayResponseType {
        return if (responseType == DoorayResponseType.IN_CHANNEL.value) DoorayResponseType.IN_CHANNEL
        else DoorayResponseType.EPHEMERAL
    }

    fun isInChannel(): Boolean {
        return responseType == DoorayResponseType.IN_CHANNEL.value
    }

    fun convertItemNo(): Long {
        return itemNo.toLongOrNull() ?: 0L
    }

    fun convertItemType(): LunchItemType {
        return LunchItemType.valueOf(itemType)
    }

    companion object {
        fun createByResponseType(responseType: DoorayResponseType): String = LunchActionSummary(
            responseType = responseType.value,
        ).toJson()

        fun addItemNo(summary: LunchActionSummary, itemNo: Long): String =
            summary.copy(itemNo = itemNo.toString()).toJson()

        fun addItemType(summary: LunchActionSummary, itemType: LunchItemType): String =
            summary.copy(itemType = itemType.name).toJson()
    }
}
