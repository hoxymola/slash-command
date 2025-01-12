package dev.weekend.slashcommand.domain.enums

/**
 * @author Jaeguk Cho
 */

enum class VoteInteractionType : InteractionType {
    OPEN_TITLE_CHANGE_DIALOG,
    CHANGE_TITLE,
    OPEN_ITEM_CHANGE_DIALOG,
    CHANGE_ITEM,
    OPEN_ITEM_ADD_DIALOG,
    ADD_ITEM,
    CHANGE_SELECTABLE_ITEM_COUNT,
    CHANGE_SHOW_PROGRESS_YN,
    START_VOTE,
    CANCEL_VOTE,
    CHECK_VOTE,
    VOTE,
    END_VOTE,
    LINK,
    ;
}
