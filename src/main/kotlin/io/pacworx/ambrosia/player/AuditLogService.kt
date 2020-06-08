package io.pacworx.ambrosia.player

import org.springframework.stereotype.Service

@Service
class AuditLogService(
    private val auditLogRepository: AuditLogRepository
) {

    fun log(player: Player, action: String, adminAction: Boolean = false, betaTesterAction: Boolean = false) {
        player.didAction()
        auditLogRepository.save(AuditLog(
            playerId = player.id,
            action = action,
            adminAction = adminAction,
            betaTesterAction = betaTesterAction
        ))
    }
}
