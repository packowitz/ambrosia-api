package io.pacworx.ambrosia.resources

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
            ResourceType.STEAL -> spendSteal(resources, amount)
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
            throw RuntimeException("You cannot spend $amount steam")
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
            throw RuntimeException("You cannot spend $amount cogwheels")
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
            throw RuntimeException("You cannot spend $amount tokens")
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
            throw RuntimeException("You cannot spend $amount coins")
        }
        resources.coins -= amount
        resources.coinsUsed += amount
        return resources
    }

    fun spendRubies(resources: Resources, amount: Int): Resources {
        if (amount > resources.rubies) {
            throw RuntimeException("You cannot spend $amount rubies")
        }
        resources.rubies -= amount
        resources.rubiesUsed += amount
        return resources
    }

    fun spendMetal(resources: Resources, amount: Int): Resources {
        if (amount > resources.metal) {
            throw RuntimeException("You cannot spend $amount metal")
        }
        resources.metal -= amount
        resources.metalUsed += amount
        return resources
    }

    fun spendIron(resources: Resources, amount: Int): Resources {
        if (amount > resources.iron) {
            throw RuntimeException("You cannot spend $amount iron")
        }
        resources.iron -= amount
        resources.ironUsed += amount
        return resources
    }

    fun spendSteal(resources: Resources, amount: Int): Resources {
        if (amount > resources.steal) {
            throw RuntimeException("You cannot spend $amount steal")
        }
        resources.steal -= amount
        resources.stealUsed += amount
        return resources
    }

    fun spendWood(resources: Resources, amount: Int): Resources {
        if (amount > resources.wood) {
            throw RuntimeException("You cannot spend $amount wood")
        }
        resources.wood -= amount
        resources.woodUsed += amount
        return resources
    }

    fun spendBrownCoal(resources: Resources, amount: Int): Resources {
        if (amount > resources.brownCoal) {
            throw RuntimeException("You cannot spend $amount brown coal")
        }
        resources.brownCoal -= amount
        resources.brownCoalUsed += amount
        return resources
    }

    fun spendBlackCoal(resources: Resources, amount: Int): Resources {
        if (amount > resources.blackCoal) {
            throw RuntimeException("You cannot spend $amount black coal")
        }
        resources.blackCoal -= amount
        resources.blackCoalUsed += amount
        return resources
    }

    fun spendSimpleGenome(resources: Resources, amount: Int): Resources {
        if (amount > resources.simpleGenome) {
            throw RuntimeException("You cannot spend $amount simple genomes")
        }
        resources.simpleGenome -= amount
        resources.simpleGenomeUsed += amount
        return resources
    }

    fun spendCommonGenome(resources: Resources, amount: Int): Resources {
        if (amount > resources.commonGenome) {
            throw RuntimeException("You cannot spend $amount common genomes")
        }
        resources.commonGenome -= amount
        resources.commonGenomeUsed += amount
        return resources
    }

    fun spendUncommonGenome(resources: Resources, amount: Int): Resources {
        if (amount > resources.uncommonGenome) {
            throw RuntimeException("You cannot spend $amount uncommon genomes")
        }
        resources.uncommonGenome -= amount
        resources.uncommonGenomeUsed += amount
        return resources
    }

    fun spendRareGenome(resources: Resources, amount: Int): Resources {
        if (amount > resources.rareGenome) {
            throw RuntimeException("You cannot spend $amount rare genomes")
        }
        resources.rareGenome -= amount
        resources.rareGenomeUsed += amount
        return resources
    }

    fun spendEpicGenome(resources: Resources, amount: Int): Resources {
        if (amount > resources.epicGenome) {
            throw RuntimeException("You cannot spend $amount epic genomes")
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
            ResourceType.STEAM_MAX -> res.stealMax += amount
            ResourceType.PREMIUM_STEAM -> res.premiumSteam = min(res.premiumSteamMax, res.premiumSteam + amount)
            ResourceType.PREMIUM_STEAM_MAX -> res.premiumSteamMax += amount
            ResourceType.COGWHEELS_MAX -> res.cogwheelsMax += amount
            ResourceType.PREMIUM_COGWHEELS -> res.premiumCogwheels = min(res.premiumCogwheelsMax, res.premiumCogwheels + amount)
            ResourceType.PREMIUM_COGWHEELS_MAX -> res.premiumCogwheelsMax += amount
            ResourceType.TOKENS_MAX -> res.tokensMax += amount
            ResourceType.PREMIUM_TOKENS -> res.premiumTokens = min(res.premiumTokensMax, res.premiumTokens + amount)
            ResourceType.PREMIUM_TOKENS_MAX -> res.premiumTokensMax += amount
            ResourceType.COINS -> res.coins += amount
            ResourceType.RUBIES -> res.rubies += amount
            ResourceType.METAL -> res.metal = min(res.metalMax, res.metal + amount)
            ResourceType.METAL_MAX -> res.metalMax += amount
            ResourceType.IRON -> res.iron = min(res.ironMax, res.iron + amount)
            ResourceType.IRON_MAX -> res.ironMax += amount
            ResourceType.STEAL -> res.steal = min(res.stealMax, res.steal + amount)
            ResourceType.STEAL_MAX -> res.stealMax += amount
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
