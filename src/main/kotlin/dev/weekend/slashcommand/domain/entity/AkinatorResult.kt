package dev.weekend.slashcommand.domain.entity

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType.IDENTITY
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.eu.zajc.akiwrapper.core.entities.impl.GuessImpl

/**
 * @author Jaeguk Cho
 */

@Entity
@Table(name = "akinator_result")
data class AkinatorResult(
    @Id
    @GeneratedValue(strategy = IDENTITY)
    val akinatorNo: Long = 0,

    val userId: String,

    var name: String? = null,

    var description: String? = null,

    var image: String? = null,
) : BaseTimeEntity() {
    fun updateResult(
        akinator: GuessImpl,
    ) {
        name = akinator.name
        description = akinator.description
        image = akinator.image.toString()
    }

    companion object {
        fun createBy(
            userId: String,
        ) = AkinatorResult(
            userId = userId,
        )
    }
}
