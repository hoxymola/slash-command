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
) {
    ISTJ(I, S, T, J),
    ISTP(I, S, T, P),
    ISFJ(I, S, F, J),
    ISFP(I, S, F, P),
    INTJ(I, N, T, J),
    INTP(I, N, T, P),
    INFJ(I, N, F, J),
    INFP(I, N, F, P),
    ESTJ(E, S, T, J),
    ESTP(E, S, T, P),
    ESFJ(E, S, F, J),
    ESFP(E, S, F, P),
    ENTJ(E, N, T, J),
    ENTP(E, N, T, P),
    ENFJ(E, N, F, J),
    ENFP(E, N, F, P),
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
