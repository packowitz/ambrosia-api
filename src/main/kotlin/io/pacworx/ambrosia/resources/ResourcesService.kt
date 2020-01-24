package io.pacworx.ambrosia.resources

import io.pacworx.ambrosia.player.Player
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

@Service
class ResourcesService(private val resourcesRepository: ResourcesRepository) {



    fun getResources(player: Player): Resources {
        val resources = resourcesRepository.getOne(player.id)
        calcProduction(resources)
        return resources
    }

    fun calcProduction(resources: Resources) {
        if (resources.steam < resources.steamMax) {
            val now = LocalDateTime.now()
            val gained = resources.steamLastProduction.until(now, ChronoUnit.SECONDS) / resources.steamProduceSeconds
            if ((resources.steam + gained) >= resources.steamMax) {
                resources.steam = resources.steamMax
                resources.steamLastProduction = now
            } else {
                resources.steam += gained.toInt()
                resources.steamLastProduction = resources.steamLastProduction.plusSeconds(gained * resources.steamProduceSeconds)
            }
        }
        if (resources.cogwheels < resources.cogwheelsMax) {
            val now = LocalDateTime.now()
            val gained = resources.cogwheelsLastProduction.until(now, ChronoUnit.SECONDS) / resources.cogwheelsProduceSeconds
            if ((resources.cogwheels + gained) >= resources.cogwheelsMax) {
                resources.cogwheels = resources.cogwheelsMax
                resources.cogwheelsLastProduction = now
            } else {
                resources.cogwheels += gained.toInt()
                resources.cogwheelsLastProduction = resources.cogwheelsLastProduction.plusSeconds(gained * resources.cogwheelsProduceSeconds)
            }
        }
        if (resources.tokens < resources.tokensMax) {
            val now = LocalDateTime.now()
            val gained = resources.tokensLastProduction.until(now, ChronoUnit.SECONDS) / resources.tokensProduceSeconds
            if ((resources.tokens + gained) >= resources.tokensMax) {
                resources.tokens = resources.tokensMax
                resources.tokensLastProduction = now
            } else {
                resources.tokens += gained.toInt()
                resources.tokensLastProduction = resources.tokensLastProduction.plusSeconds(gained * resources.tokensProduceSeconds)
            }
        }
    }

    fun spendSteam(player: Player, amount: Int): Resources =
        spendSteam(resourcesRepository.getOne(player.id), amount)

    fun spendSteam(resources: Resources, amount: Int): Resources {
        calcProduction(resources)
        if (amount > resources.steam + resources.premiumSteam) {
            throw RuntimeException("Player ${resources.playerId} cannot spend $amount steam")
        }
        resources.steam -= amount
        if (resources.steam < 0) {
            resources.premiumSteam += resources.steam
            resources.steam = 0
        }
        return resources
    }
}