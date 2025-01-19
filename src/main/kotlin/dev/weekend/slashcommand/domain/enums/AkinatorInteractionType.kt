package dev.weekend.slashcommand.domain.enums

/**
 * @author Jaeguk Cho
 */

enum class AkinatorInteractionType: InteractionType {
    START_AKINATOR,
    CONTINUE_AKINATOR,
    CANCEL_AKINATOR,
    ANSWER_QUESTION,
    UNDO_ANSWER,
    CONFIRM_GUESS,
    REJECT_GUESS,
    SHARE_GUESS,
    ;
}
