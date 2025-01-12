package dev.weekend.slashcommand.domain.enums

/**
 * @author Jaeguk Cho
 */

enum class MbtiInteractionType : InteractionType {
    START_TEST,
    CANCEL_TEST,
    FIRST_ANSWER,
    SECOND_ANSWER,
    NEXT_QUESTION,
    PREV_QUESTION,
    GET_STATISTICS,
    SHARE_STATISTICS,
    ;
}
