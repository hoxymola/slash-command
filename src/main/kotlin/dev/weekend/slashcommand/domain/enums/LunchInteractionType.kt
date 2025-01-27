package dev.weekend.slashcommand.domain.enums

/**
 * @author Yoohwa Cho
 */
enum class LunchInteractionType : InteractionType {
    START, //추천 시작
    GET_RECOMMENDATION, //추천 받기
    START_DETAIL_RECOMMEND, //타입에 따라 추천 받기
    RECOMMEND_AGAIN, //추천 다시 받기
    CONFIRM_RECOMMEND, //추천 확정하기
    RESTART, //처음으로 가기
    CANCEL, //취소하기
    ;
}