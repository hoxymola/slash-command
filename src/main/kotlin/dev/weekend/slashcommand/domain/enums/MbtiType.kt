package dev.weekend.slashcommand.domain.enums

import dev.weekend.slashcommand.domain.enums.MbtiTrait.*

/**
 * @author Jaeguk Cho
 */

enum class MbtiType(
    val firstTrait: MbtiTrait,
    val secondTrait: MbtiTrait,
    val thirdTrait: MbtiTrait,
    val fourthTrait: MbtiTrait,
    val boldText: String,
) {
    ISTJ(I, S, T, J, "𝗜𝗦𝗧𝗝"),
    ISTP(I, S, T, P, "𝗜𝗦𝗧𝗣"),
    ISFJ(I, S, F, J, "𝗜𝗦𝗙𝗝"),
    ISFP(I, S, F, P, "𝗜𝗦𝗙𝗣"),
    INTJ(I, N, T, J, "𝗜𝗡𝗧𝗝"),
    INTP(I, N, T, P, "𝗜𝗡𝗧𝗣"),
    INFJ(I, N, F, J, "𝗜𝗡𝗙𝗝"),
    INFP(I, N, F, P, "𝗜𝗡𝗙𝗣"),
    ESTJ(E, S, T, J, "𝗘𝗦𝗧𝗝"),
    ESTP(E, S, T, P, "𝗘𝗦𝗧𝗣"),
    ESFJ(E, S, F, J, "𝗘𝗦𝗙𝗝"),
    ESFP(E, S, F, P, "𝗘𝗦𝗙𝗣"),
    ENTJ(E, N, T, J, "𝗘𝗡𝗧𝗝"),
    ENTP(E, N, T, P, "𝗘𝗡𝗧𝗣"),
    ENFJ(E, N, F, J, "𝗘𝗡𝗙𝗝"),
    ENFP(E, N, F, P, "𝗘𝗡𝗙𝗣"),
    ;

    companion object {
        fun getByTraits(
            firstTrait: MbtiTrait,
            secondTrait: MbtiTrait,
            thirdTrait: MbtiTrait,
            fourthTrait: MbtiTrait,
        ): MbtiType {
            return entries.first { mbti ->
                listOf(
                    mbti.firstTrait == firstTrait,
                    mbti.secondTrait == secondTrait,
                    mbti.thirdTrait == thirdTrait,
                    mbti.fourthTrait == fourthTrait,
                ).all { it }
            }
        }
    }
}
