package dev.weekend.slashcommand.domain.repository

import dev.weekend.slashcommand.domain.entity.Command
import org.springframework.data.jpa.repository.JpaRepository

/**
 * @author Jaeguk Cho
 */

interface CommandRepository : JpaRepository<Command, Long> {
}
