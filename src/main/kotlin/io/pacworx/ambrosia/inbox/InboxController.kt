package io.pacworx.ambrosia.inbox

import io.pacworx.ambrosia.common.PlayerActionResponse
import io.pacworx.ambrosia.exceptions.EntityNotFoundException
import io.pacworx.ambrosia.exceptions.UnauthorizedException
import io.pacworx.ambrosia.player.Player
import org.springframework.data.repository.findByIdOrNull
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime
import javax.transaction.Transactional

@RestController
@CrossOrigin(maxAge = 3600)
@RequestMapping("inbox")
class InboxController(
    private val inboxMessageRepository: InboxMessageRepository,
    private val inboxService: InboxService
) {

    @PostMapping("claim/{messageId}")
    @Transactional
    fun claimGoods(@ModelAttribute("player") player: Player, @PathVariable messageId: Long): PlayerActionResponse {
        val timestamp = LocalDateTime.now()
        val inboxMessage = inboxMessageRepository.findByIdOrNull(messageId)
            ?: throw EntityNotFoundException(player, "inboxMessage", messageId)
        if (inboxMessage.playerId != player.id) {
            throw UnauthorizedException(player, "Cannot claim inbox item that you don't own")
        }
        inboxMessage.items.forEach { inboxService.claimInboxItem(player, it) }

        val deletedMessage = inboxMessage.takeIf { it.messageType == InboxMessageType.GOODS }
            ?.let {
                inboxMessageRepository.delete(it)
                it.id
            }

        return PlayerActionResponse(
            inboxMessages = inboxMessageRepository.findAllByPlayerIdAndSendTimestampIsAfter(player.id, timestamp.minusSeconds(1)),
            inboxMessageDeleted = deletedMessage
        )
    }
}