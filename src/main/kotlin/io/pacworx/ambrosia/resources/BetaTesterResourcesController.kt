package io.pacworx.ambrosia.resources

import io.pacworx.ambrosia.common.PlayerActionResponse
import io.pacworx.ambrosia.player.AuditLogService
import io.pacworx.ambrosia.player.Player
import org.springframework.web.bind.annotation.*
import javax.transaction.Transactional

@RestController
@CrossOrigin(maxAge = 3600)
@RequestMapping("tester/resources")
class BetaTesterResourcesController(private val resourcesService: ResourcesService,
                                    private val auditLogService: AuditLogService) {

    @PostMapping("gain/{resourceType}")
    @Transactional
    fun gainResources(@ModelAttribute("player") player: Player,
                      @PathVariable resourceType: ResourceType): PlayerActionResponse {
        val resources = resourcesService.getResources(player)

        when(resourceType) {
            ResourceType.STEAM -> resources.steam = resources.steamMax
            ResourceType.PREMIUM_STEAM -> resources.premiumSteam = resources.premiumSteamMax
            ResourceType.COGWHEELS -> resources.cogwheels = resources.cogwheelsMax
            ResourceType.PREMIUM_COGWHEELS -> resources.premiumCogwheels = resources.premiumCogwheelsMax
            ResourceType.TOKENS -> resources.tokens = resources.tokensMax
            ResourceType.PREMIUM_TOKENS -> resources.premiumTokens = resources.premiumTokensMax
            ResourceType.COINS -> resources.coins += 1000
            ResourceType.RUBIES -> resources.rubies += 50
            ResourceType.METAL -> resources.metal = resources.metalMax
            ResourceType.IRON -> resources.iron = resources.ironMax
            ResourceType.STEEL -> resources.steel = resources.steelMax
            ResourceType.WOOD -> resources.wood = resources.woodMax
            ResourceType.BROWN_COAL -> resources.brownCoal = resources.brownCoalMax
            ResourceType.BLACK_COAL -> resources.blackCoal = resources.blackCoalMax
            else -> {}
        }

        auditLogService.log(player, "Refills resource ${resourceType.name}", betaTesterAction = true)

        return PlayerActionResponse(resources = resources)
    }
}
