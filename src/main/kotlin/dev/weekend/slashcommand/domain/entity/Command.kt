package dev.weekend.slashcommand.domain.entity

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType.IDENTITY
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.hibernate.annotations.DynamicUpdate

/**
 * @author Jaeguk Cho
 */

@Entity
@Table(name = "command")
data class Command(
    @Id
    @GeneratedValue(strategy = IDENTITY)
    val commandNo: Long = 0,

    val commandName: String,
) : BaseTimeEntity()
