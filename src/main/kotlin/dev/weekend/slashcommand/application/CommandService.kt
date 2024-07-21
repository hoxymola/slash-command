package dev.weekend.slashcommand.application

import dev.weekend.slashcommand.domain.entity.Command
import dev.weekend.slashcommand.domain.repository.CommandRepository
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

/**
 * @author Jaeguk Cho
 */

@Service
class CommandService(
    private val commandRepository: CommandRepository,
) {
    fun getCommand(
        commandNo: Long,
    ): Command {
        return commandRepository.findByIdOrNull(commandNo)
            ?: throw NotFoundException()
    }

    fun addCommand(
        commandName: String,
    ): Command {
        return commandRepository.save(
            Command(
                commandName = commandName,
            )
        )
    }
}
