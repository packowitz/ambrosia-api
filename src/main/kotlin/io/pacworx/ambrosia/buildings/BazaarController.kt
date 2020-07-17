package io.pacworx.ambrosia.buildings

import io.pacworx.ambrosia.common.PlayerActionResponse
import io.pacworx.ambrosia.player.Player
import io.pacworx.ambrosia.progress.ProgressRepository
import io.pacworx.ambrosia.resources.ResourcesService
import org.springframework.web.bind.annotation.*
import javax.transaction.Transactional

@RestController
@CrossOrigin(maxAge = 3600)
@RequestMapping("bazaar")
class BazaarController(
    val progressRepository: ProgressRepository,
    val resourcesService: ResourcesService
) {

    @PostMapping("trade/{trade}")
    @Transactional
    fun trade(@ModelAttribute("player") player: Player, @PathVariable trade: Trade): PlayerActionResponse {
        val progress = progressRepository.getOne(player.id)
        val resources = resourcesService.getResources(player)
        resourcesService.spendResource(resources, trade.giveType, trade.giveAmount - (progress.negotiationLevel * trade.negotiationGiveReduction))
        resourcesService.gainResources(resources, trade.getType, trade.getAmount + (progress.negotiationLevel * trade.negotiationGetIncrease))
        return PlayerActionResponse(resources = resources)
    }
}