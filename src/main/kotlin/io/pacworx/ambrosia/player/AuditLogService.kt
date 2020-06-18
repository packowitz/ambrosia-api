package io.pacworx.ambrosia.player

import org.springframework.stereotype.Service
import javax.transaction.Transactional

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

    fun log(playerId: Long, action: String, adminAction: Boolean = false, betaTesterAction: Boolean = false) {
        auditLogRepository.save(AuditLog(
            playerId = playerId,
            action = action,
            adminAction = adminAction,
            betaTesterAction = betaTesterAction
        ))
    }

    @Transactional(Transactional.TxType.REQUIRES_NEW)
    fun logError(playerId: Long = 1, action: String) {
        auditLogRepository.save(AuditLog(
            playerId = playerId,
            action = action,
            error = true
        ))
    }
}
