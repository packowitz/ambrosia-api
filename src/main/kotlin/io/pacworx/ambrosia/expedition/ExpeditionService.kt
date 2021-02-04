package io.pacworx.ambrosia.expedition

import io.pacworx.ambrosia.player.Player
import io.pacworx.ambrosia.speedup.SpeedupService
import io.pacworx.ambrosia.speedup.SpeedupType
import org.springframework.stereotype.Service

@Service
class ExpeditionService(
    private val expeditionRepository: ExpeditionRepository,
    private val playerExpeditionRepository: PlayerExpeditionRepository,
    private val speedupService: SpeedupService
) {

    fun expeditionsByLevel(expeditionLevel: Int): List<Expedition> = expeditionRepository.findAllByExpeditionBase_LevelAndActiveIsTrue(expeditionLevel)

    fun getAllPlayerExpeditions(player: Player): List<PlayerExpedition> {
        return playerExpeditionRepository.findAllByPlayerIdOrderByStartTimestamp(player.id).onEach { enrichSpeedup(it) }
    }

    fun availableExpeditions(player: Player, expeditionLevel: Int): List<Expedition> {
        if (expeditionLevel > 0) {
            val expeditions = expeditionsByLevel(expeditionLevel)
            val inProgress = playerExpeditionRepository.findAllByPlayerIdAndExpeditionIdIn(player.id, expeditions.map { it.id })
            return expeditions.filter { exp -> inProgress.none { exp.id == it.expeditionId } }
        } else {
            return emptyList()
        }
    }

    fun enrichSpeedup(playerExpedition: PlayerExpedition) {
        if (playerExpedition.getSecondsUntilDone() > 0) {
            playerExpedition.speedup = speedupService.speedup(SpeedupType.EXPEDITION, playerExpedition.getDuration(), playerExpedition.getSecondsUntilDone())
        } else {
            playerExpedition.speedup = null
        }
    }
}