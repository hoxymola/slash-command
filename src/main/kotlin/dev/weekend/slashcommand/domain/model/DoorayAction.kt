package dev.weekend.slashcommand.domain.model

import dev.weekend.slashcommand.domain.enums.DoorayActionType.BUTTON
import dev.weekend.slashcommand.domain.enums.DoorayButtonStyle
import dev.weekend.slashcommand.domain.enums.DoorayButtonStyle.DEFAULT
import dev.weekend.slashcommand.domain.enums.VoteInteractionType

/**
 * @author Jaeguk Cho
 */

data class DoorayAction(
    val name: Any,
    val type: String,
    val text: String,
    val value: String? = null,
    val style: String? = null,
    val options: List<DoorayOption>? = null,
) {
    companion object {
        fun createButton(
            name: VoteInteractionType,
            text: String,
            value: String = "",
            style: DoorayButtonStyle? = DEFAULT,
        ) = DoorayAction(
            name = name,
            type = BUTTON.value,
            text = text,
            value = value,
            style = style?.value,
        )
    }
}
