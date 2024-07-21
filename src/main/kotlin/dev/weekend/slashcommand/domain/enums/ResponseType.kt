package dev.weekend.slashcommand.domain.enums

/**
 * @author Jaeguk Cho
 */

enum class ResponseType(
    val value: String,
) {
    IN_CHANNEL("inChannel"),
    EPHEMERAL("ephemeral"),
    ;
}
