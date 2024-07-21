package dev.weekend.slashcommand.domain.entity

import jakarta.persistence.EntityListeners
import jakarta.persistence.MappedSuperclass
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime

/**
 * @author Jaeguk Cho
 */

@MappedSuperclass
@EntityListeners(AuditingEntityListener::class)
abstract class BaseTimeEntity {
    @CreatedDate
    open var registerYmdt: LocalDateTime = LocalDateTime.MIN

    @LastModifiedDate
    open var updateYmdt: LocalDateTime = LocalDateTime.MIN
}
