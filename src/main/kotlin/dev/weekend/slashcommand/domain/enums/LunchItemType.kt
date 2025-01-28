package dev.weekend.slashcommand.domain.enums

/**
 * @author Yoohwa Cho
 */
enum class LunchItemType(
    val label: String = "",
) {
    KOREAN("한식"),
    CHINESE("중식"),
    JAPANESE("일식"),

    SNACK("분식"),

    CAFETERIA("구내식당"),
    FASTFOOD("패스트푸드"),
    SALAD("샐러드/카페"),

    ETC("기타"),

    BUFFET("뷔페"),
    ;
}
