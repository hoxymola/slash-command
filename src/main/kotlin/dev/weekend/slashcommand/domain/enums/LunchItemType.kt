package dev.weekend.slashcommand.domain.enums

import dev.weekend.slashcommand.domain.constant.LunchConstant.BUFFET_EMOJIS
import dev.weekend.slashcommand.domain.constant.LunchConstant.CAFETERIA_EMOJIS
import dev.weekend.slashcommand.domain.constant.LunchConstant.CHINESE_EMOJIS
import dev.weekend.slashcommand.domain.constant.LunchConstant.ETC_EMOJIS
import dev.weekend.slashcommand.domain.constant.LunchConstant.FASTFOOD_EMOJIS
import dev.weekend.slashcommand.domain.constant.LunchConstant.JAPANESE_EMOJIS
import dev.weekend.slashcommand.domain.constant.LunchConstant.KOREAN_EMOJIS
import dev.weekend.slashcommand.domain.constant.LunchConstant.SALAD_EMOJIS
import dev.weekend.slashcommand.domain.constant.LunchConstant.SNACK_EMOJIS
import dev.weekend.slashcommand.domain.extension.getRandom

/**
 * @author Yoohwa Cho
 */
enum class LunchItemType(
    val label: String = "",
    val emojis: List<String> = emptyList(),
) {
    KOREAN("한식", KOREAN_EMOJIS),
    CHINESE("중식", CHINESE_EMOJIS),
    JAPANESE("일식", JAPANESE_EMOJIS),

    SNACK("분식", SNACK_EMOJIS),

    CAFETERIA("구내식당", CAFETERIA_EMOJIS),
    FASTFOOD("패스트푸드", FASTFOOD_EMOJIS),
    SALAD("샐러드/카페", SALAD_EMOJIS),

    ETC("기타", ETC_EMOJIS),

    BUFFET("뷔페", BUFFET_EMOJIS),
    ;

    fun getEmoji(): String {
        val buffetRange = (2..3)
        return when (this) {
            BUFFET -> this.emojis.getRandom(buffetRange.random())
            else -> this.emojis.getRandom(1)
        }
    }
}
