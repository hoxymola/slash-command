package dev.weekend.slashcommand.domain.enums

/**
 * @author Jaeguk Cho
 */

enum class AkinatorAnswerType(
    val value: String,
) {
    YES("예"),
    NO("아니요"),
    DONT_KNOW("모르겠습니다"),
    PROBABLY("그럴 겁니다"),
    PROBABLY_NOT("아닐 겁니다"),
    ;

    companion object {
        fun getByName(name: String) = entries.firstOrNull { it.name == name }
    }
}

