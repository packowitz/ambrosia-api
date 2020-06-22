package io.pacworx.ambrosia.resources

import io.pacworx.ambrosia.exceptions.InsufficientResourcesException
import io.pacworx.ambrosia.player.Player
import org.springframework.stereotype.Service
import java.lang.Integer.min
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

    fun spendResource(player: Player, type: ResourceType, amount: Int): Resources =
        spendResource(resourcesRepository.getOne(player.id), type, amount)

    fun spendResource(resources: Resources, type: ResourceType, amount: Int): Resources {
        return when(type) {
            ResourceType.STEAM -> spendSteam(resources, amount)
            ResourceType.COGWHEELS -> spendCogwheels(resources, amount)
            ResourceType.TOKENS -> spendTokens(resources, amount)
            ResourceType.COINS -> spendCoins(resources, amount)
            ResourceType.RUBIES -> spendRubies(resources, amount)
            ResourceType.METAL -> spendMetal(resources, amount)
            ResourceType.IRON -> spendIron(resources, amount)
            ResourceType.STEEL -> spendSteel(resources, amount)
            ResourceType.WOOD -> spendWood(resources, amount)
            ResourceType.BROWN_COAL -> spendBrownCoal(resources, amount)
            ResourceType.BLACK_COAL -> spendBlackCoal(resources, amount)
            ResourceType.SIMPLE_GENOME -> spendSimpleGenome(resources, amount)
            ResourceType.COMMON_GENOME -> spendCommonGenome(resources, amount)
            ResourceType.UNCOMMON_GENOME -> spendUncommonGenome(resources, amount)
            ResourceType.RARE_GENOME -> spendRareGenome(resources, amount)
            ResourceType.EPIC_GENOME -> spendEpicGenome(resources, amount)
            else -> throw RuntimeException("Unimplemented resource to spend: $type")
        }
    }

    fun spendSteam(player: Player, amount: Int): Resources =
        spendSteam(resourcesRepository.getOne(player.id), amount)

    fun spendSteam(resources: Resources, amount: Int): Resources {
        calcProduction(resources)
        if (amount > resources.steam + resources.premiumSteam) {
            throw InsufficientResourcesException(resources.playerId, "Steam", amount)
        }
        if (resources.steam >= resources.steamMax) {
            resources.steamLastProduction = LocalDateTime.now()
        }
        resources.steam -= amount
        resources.steamUsed += amount
        if (resources.steam < 0) {
            resources.premiumSteam += resources.steam
            resources.steamUsed += resources.steam
            resources.premiumSteamUsed -= resources.steam
            resources.steam = 0
        }
        return resources
    }

    fun spendCogwheels(player: Player, amount: Int): Resources =
        spendCogwheels(resourcesRepository.getOne(player.id), amount)

    fun spendCogwheels(resources: Resources, amount: Int): Resources {
        calcProduction(resources)
        if (amount > resources.cogwheels + resources.premiumCogwheels) {
            throw InsufficientResourcesException(resources.playerId, "Cogwheels", amount)
        }
        if (resources.cogwheels >= resources.cogwheelsMax) {
            resources.cogwheelsLastProduction = LocalDateTime.now()
        }
        resources.cogwheels -= amount
        resources.cogwheelsUsed += amount
        if (resources.cogwheels < 0) {
            resources.premiumCogwheels += resources.cogwheels
            resources.cogwheelsUsed += resources.cogwheels
            resources.premiumCogwheelsUsed -= resources.cogwheels
            resources.cogwheels = 0
        }
        return resources
    }

    fun spendTokens(player: Player, amount: Int): Resources =
        spendTokens(resourcesRepository.getOne(player.id), amount)

    fun spendTokens(resources: Resources, amount: Int): Resources {
        calcProduction(resources)
        if (amount > resources.tokens + resources.premiumTokens) {
            throw InsufficientResourcesException(resources.playerId, "Tokens", amount)
        }
        if (resources.tokens >= resources.tokensMax) {
            resources.tokensLastProduction = LocalDateTime.now()
        }
        resources.tokens -= amount
        resources.tokensUsed += amount
        if (resources.tokens < 0) {
            resources.premiumTokens += resources.tokens
            resources.tokensUsed += resources.tokens
            resources.premiumTokensUsed -= resources.tokens
            resources.tokens = 0
        }
        return resources
    }

    fun spendCoins(resources: Resources, amount: Int): Resources {
        if (amount > resources.coins) {
            throw InsufficientResourcesException(resources.playerId, "Coins", amount)
        }
        resources.coins -= amount
        resources.coinsUsed += amount
        return resources
    }

    fun spendRubies(resources: Resources, amount: Int): Resources {
        if (amount > resources.rubies) {
            throw InsufficientResourcesException(resources.playerId, "Rubies", amount)
        }
        resources.rubies -= amount
        resources.rubiesUsed += amount
        return resources
    }

    fun spendMetal(resources: Resources, amount: Int): Resources {
        if (amount > resources.metal) {
            throw InsufficientResourcesException(resources.playerId, "Metal", amount)
        }
        resources.metal -= amount
        resources.metalUsed += amount
        return resources
    }

    fun spendIron(resources: Resources, amount: Int): Resources {
        if (amount > resources.iron) {
            throw InsufficientResourcesException(resources.playerId, "Iron", amount)
        }
        resources.iron -= amount
        resources.ironUsed += amount
        return resources
    }

    fun spendSteel(resources: Resources, amount: Int): Resources {
        if (amount > resources.steel) {
            throw InsufficientResourcesException(resources.playerId, "Steel", amount)
        }
        resources.steel -= amount
        resources.steelUsed += amount
        return resources
    }

    fun spendWood(resources: Resources, amount: Int): Resources {
        if (amount > resources.wood) {
            throw InsufficientResourcesException(resources.playerId, "Wood", amount)
        }
        resources.wood -= amount
        resources.woodUsed += amount
        return resources
    }

    fun spendBrownCoal(resources: Resources, amount: Int): Resources {
        if (amount > resources.brownCoal) {
            throw InsufficientResourcesException(resources.playerId, "Brown coal", amount)
        }
        resources.brownCoal -= amount
        resources.brownCoalUsed += amount
        return resources
    }

    fun spendBlackCoal(resources: Resources, amount: Int): Resources {
        if (amount > resources.blackCoal) {
            throw InsufficientResourcesException(resources.playerId, "Black coal", amount)
        }
        resources.blackCoal -= amount
        resources.blackCoalUsed += amount
        return resources
    }

    fun spendSimpleGenome(resources: Resources, amount: Int): Resources {
        if (amount > resources.simpleGenome) {
            throw InsufficientResourcesException(resources.playerId, "Simple genomes", amount)
        }
        resources.simpleGenome -= amount
        resources.simpleGenomeUsed += amount
        return resources
    }

    fun spendCommonGenome(resources: Resources, amount: Int): Resources {
        if (amount > resources.commonGenome) {
            throw InsufficientResourcesException(resources.playerId, "Common genomes", amount)
        }
        resources.commonGenome -= amount
        resources.commonGenomeUsed += amount
        return resources
    }

    fun spendUncommonGenome(resources: Resources, amount: Int): Resources {
        if (amount > resources.uncommonGenome) {
            throw InsufficientResourcesException(resources.playerId, "Uncommon genomes", amount)
        }
        resources.uncommonGenome -= amount
        resources.uncommonGenomeUsed += amount
        return resources
    }

    fun spendRareGenome(resources: Resources, amount: Int): Resources {
        if (amount > resources.rareGenome) {
            throw InsufficientResourcesException(resources.playerId, "Rare genomes", amount)
        }
        resources.rareGenome -= amount
        resources.rareGenomeUsed += amount
        return resources
    }

    fun spendEpicGenome(resources: Resources, amount: Int): Resources {
        if (amount > resources.epicGenome) {
            throw InsufficientResourcesException(resources.playerId, "Epic genomes", amount)
        }
        resources.epicGenome -= amount
        resources.epicGenomeUsed += amount
        return resources
    }

    fun gainResources(player: Player, type: ResourceType, amount: Int): Resources {
        val res = getResources(player)
        return gainResources(res, type, amount)
    }

    fun gainResources(res: Resources, type: ResourceType, amount: Int): Resources {
        when (type) {
            ResourceType.STEAM -> res.steam += amount
            ResourceType.STEAM_MAX -> res.steamMax += amount
            ResourceType.PREMIUM_STEAM -> res.premiumSteam = min(res.premiumSteamMax, res.premiumSteam + amount)
            ResourceType.PREMIUM_STEAM_MAX -> res.premiumSteamMax += amount
            ResourceType.COGWHEELS -> res.cogwheels += amount
            ResourceType.COGWHEELS_MAX -> res.cogwheelsMax += amount
            ResourceType.PREMIUM_COGWHEELS -> res.premiumCogwheels = min(res.premiumCogwheelsMax, res.premiumCogwheels + amount)
            ResourceType.PREMIUM_COGWHEELS_MAX -> res.premiumCogwheelsMax += amount
            ResourceType.TOKENS -> res.tokens += amount
            ResourceType.TOKENS_MAX -> res.tokensMax += amount
            ResourceType.PREMIUM_TOKENS -> res.premiumTokens = min(res.premiumTokensMax, res.premiumTokens + amount)
            ResourceType.PREMIUM_TOKENS_MAX -> res.premiumTokensMax += amount
            ResourceType.COINS -> res.coins += amount
            ResourceType.RUBIES -> res.rubies += amount
            ResourceType.METAL -> res.metal = min(res.metalMax, res.metal + amount)
            ResourceType.METAL_MAX -> res.metalMax += amount
            ResourceType.IRON -> res.iron = min(res.ironMax, res.iron + amount)
            ResourceType.IRON_MAX -> res.ironMax += amount
            ResourceType.STEEL -> res.steel = min(res.steelMax, res.steel + amount)
            ResourceType.STEEL_MAX -> res.steelMax += amount
            ResourceType.WOOD -> res.wood = min(res.woodMax, res.wood + amount)
            ResourceType.WOOD_MAX -> res.woodMax += amount
            ResourceType.BROWN_COAL -> res.brownCoal = min(res.brownCoalMax, res.brownCoal + amount)
            ResourceType.BROWN_COAL_MAX -> res.brownCoalMax += amount
            ResourceType.BLACK_COAL -> res.blackCoal = min(res.blackCoalMax, res.blackCoal + amount)
            ResourceType.BLACK_COAL_MAX -> res.blackCoalMax += amount
            ResourceType.SIMPLE_GENOME -> res.simpleGenome += amount
            ResourceType.COMMON_GENOME -> res.commonGenome += amount
            ResourceType.UNCOMMON_GENOME -> res.uncommonGenome += amount
            ResourceType.RARE_GENOME -> res.rareGenome += amount
            ResourceType.EPIC_GENOME -> res.epicGenome += amount
            else -> {}
        }
        return res
    }
}
