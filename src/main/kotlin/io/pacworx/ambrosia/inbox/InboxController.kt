package io.pacworx.ambrosia.inbox

import io.pacworx.ambrosia.common.PlayerActionResponse
import io.pacworx.ambrosia.exceptions.EntityNotFoundException
import io.pacworx.ambrosia.exceptions.UnauthorizedException
import io.pacworx.ambrosia.player.Player
import org.springframework.data.repository.findByIdOrNull
import org.springframework.web.bind.annotation.*
import javax.transaction.Transactional

@RestController
@CrossOrigin(maxAge = 3600)
@RequestMapping("inbox")
class InboxController(
    private val inboxMessageRepository: InboxMessageRepository
) {

    @PostMapping("claim/{messageId}")
    @Transactional
    fun claimGoods(@ModelAttribute("player") player: Player, @PathVariable messageId: Long): PlayerActionResponse {
        val inboxMessage = inboxMessageRepository.findByIdOrNull(messageId)
            ?: throw EntityNotFoundException(player, "inboxMessage", messageId)
        if (inboxMessage.playerId != player.id) {
            throw UnauthorizedException(player, "Cannot claim inbox item that you don't own")
        }
    }
}