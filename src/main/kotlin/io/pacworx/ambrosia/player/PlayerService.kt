package io.pacworx.ambrosia.player

import com.google.common.hash.Hashing
import io.pacworx.ambrosia.battle.BattleRepository
import io.pacworx.ambrosia.battle.BattleStatus
import io.pacworx.ambrosia.battle.offline.MissionService
import io.pacworx.ambrosia.buildings.Building
import io.pacworx.ambrosia.buildings.BuildingRepository
import io.pacworx.ambrosia.buildings.BuildingType
import io.pacworx.ambrosia.common.PlayerActionResponse
import io.pacworx.ambrosia.gear.GearRepository
import io.pacworx.ambrosia.gear.JewelryRepository
import io.pacworx.ambrosia.hero.HeroRepository
import io.pacworx.ambrosia.hero.HeroService
import io.pacworx.ambrosia.maps.MapService
import io.pacworx.ambrosia.maps.SimplePlayerMapRepository
import io.pacworx.ambrosia.progress.Progress
import io.pacworx.ambrosia.progress.ProgressRepository
import io.pacworx.ambrosia.properties.PropertyService
import io.pacworx.ambrosia.properties.PropertyType
import io.pacworx.ambrosia.resources.ResourceType
import io.pacworx.ambrosia.resources.Resources
import io.pacworx.ambrosia.resources.ResourcesRepository
import io.pacworx.ambrosia.resources.ResourcesService
import io.pacworx.ambrosia.vehicle.VehiclePartRepository
import io.pacworx.ambrosia.vehicle.VehicleRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.nio.charset.StandardCharsets


@Service
class PlayerService(private val playerRepository: PlayerRepository,
                    private val progressRepository: ProgressRepository,
                    private val heroService: HeroService,
                    private val heroRepository: HeroRepository,
                    private val gearRepository: GearRepository,
                    private val jewelryRepository: JewelryRepository,
                    private val battleRepository: BattleRepository,
                    private val simplePlayerMapRepository: SimplePlayerMapRepository,
                    private val buildingRepository: BuildingRepository,
                    private val mapService: MapService,
                    private val propertyService: PropertyService,
                    private val resourcesRepository: ResourcesRepository,
                    private val resourcesService: ResourcesService,
                    private val vehicleRepository: VehicleRepository,
                    private val vehiclePartRepository: VehiclePartRepository,
                    private val missionService: MissionService) {

    @Value("\${ambrosia.pw-salt-one}")
    private lateinit var pwSalt1: String
    @Value("\${ambrosia.pw-salt-two}")
    private lateinit var pwSalt2: String

    fun signup(name: String, email: String, password: String, serviceAccount: Boolean = false): Player {
        val player = playerRepository.save(Player(
            name = name,
            email = email,
            password = if (serviceAccount) { password } else { getHash(name, password) },
            serviceAccount = serviceAccount
        ))
        buildingRepository.save(Building(playerId = player.id, type = BuildingType.BARRACKS))
        buildingRepository.save(Building(playerId = player.id, type = BuildingType.STORAGE_0))
        buildingRepository.save(Building(playerId = player.id, type = BuildingType.FORGE))
        resourcesRepository.save(Resources(
            playerId = player.id,
            steam = getStartingAmount(ResourceType.STEAM),
            steamMax = getStartingAmount(ResourceType.STEAM_MAX),
            premiumSteam = getStartingAmount(ResourceType.PREMIUM_STEAM),
            premiumSteamMax = getStartingAmount(ResourceType.PREMIUM_STEAM_MAX),
            cogwheels = getStartingAmount(ResourceType.COGWHEELS),
            cogwheelsMax = getStartingAmount(ResourceType.COGWHEELS_MAX),
            premiumCogwheels = getStartingAmount(ResourceType.PREMIUM_COGWHEELS),
            premiumCogwheelsMax = getStartingAmount(ResourceType.PREMIUM_COGWHEELS_MAX),
            tokens = getStartingAmount(ResourceType.TOKENS),
            tokensMax = getStartingAmount(ResourceType.TOKENS_MAX),
            premiumTokens = getStartingAmount(ResourceType.PREMIUM_TOKENS),
            premiumTokensMax = getStartingAmount(ResourceType.PREMIUM_TOKENS_MAX),
            coins = getStartingAmount(ResourceType.COINS),
            rubies = getStartingAmount(ResourceType.RUBIES),
            metal = getStartingAmount(ResourceType.METAL),
            metalMax = getStartingAmount(ResourceType.METAL_MAX),
            iron = getStartingAmount(ResourceType.IRON),
            ironMax = getStartingAmount(ResourceType.IRON_MAX),
            steal = getStartingAmount(ResourceType.STEAL),
            stealMax = getStartingAmount(ResourceType.STEAL_MAX),
            wood = getStartingAmount(ResourceType.WOOD),
            woodMax = getStartingAmount(ResourceType.WOOD_MAX),
            brownCoal = getStartingAmount(ResourceType.BROWN_COAL),
            brownCoalMax = getStartingAmount(ResourceType.BROWN_COAL_MAX),
            blackCoal = getStartingAmount(ResourceType.BLACK_COAL),
            blackCoalMax = getStartingAmount(ResourceType.BLACK_COAL_MAX),
            simpleGenome = getStartingAmount(ResourceType.SIMPLE_GENOME),
            commonGenome = getStartingAmount(ResourceType.COMMON_GENOME),
            uncommonGenome = getStartingAmount(ResourceType.UNCOMMON_GENOME),
            rareGenome = getStartingAmount(ResourceType.RARE_GENOME),
            epicGenome = getStartingAmount(ResourceType.EPIC_GENOME)
        ))
        progressRepository.save(Progress(playerId = player.id))
        return player
    }

    fun getStartingAmount(type: ResourceType): Int {
        val playerLvlResources = propertyService.getAllProperties(PropertyType.PLAYER_LVL_RESOURCES)
        val storageResources = propertyService.getAllProperties(PropertyType.STORAGE_RESOURCES)
        return (playerLvlResources.find { p -> p.level == 1 && p.resourceType == type }?.value1 ?: 0) +
            (storageResources.find {p -> p.level == 1 && p.resourceType == type}?.value1 ?: 0)
    }

    fun login(email: String, password: String): Player {
        return playerRepository.findByEmailIgnoreCase(email.trim())?.takeIf { getHash(it.name, password) == it.password }
                ?: throw RuntimeException("Auth failed")
    }

    fun save(player: Player): Player {
        return playerRepository.save(player)
    }

    fun response(player: Player, token: String? = null): PlayerActionResponse {
        val progress = progressRepository.getOne(player.id)
        val resources = resourcesService.getResources(player)
        val heroes = heroRepository.findAllByPlayerIdOrderByLevelDescStarsDescHeroBase_IdAscIdAsc(player.id)
            .map { heroService.asHeroDto(it) }
        val gears = gearRepository.findAllByPlayerIdAndEquippedToIsNull(player.id)
        val jewelries = jewelryRepository.findAllByPlayerId(player.id)
        val buildings = buildingRepository.findAllByPlayerId(player.id)
        val vehicles = vehicleRepository.findAllByPlayerId(player.id)
        val vehicleParts = vehiclePartRepository.findAllByPlayerIdAndEquippedToIsNull(player.id)
        val playerMaps = simplePlayerMapRepository.findAllByPlayerId(player.id)
        val currentMap = mapService.getCurrentPlayerMap(player)
        val missions = missionService.getAllMissions(player)
        val ongoingBattle = battleRepository.findTopByPlayerIdAndStatusInAndPreviousBattleIdNull(player.id, listOf(BattleStatus.INIT, BattleStatus.PLAYER_TURN, BattleStatus.OPP_TURN, BattleStatus.STAGE_PASSED))
        return PlayerActionResponse(
            resources = resources,
            token = token,
            player = player,
            progress = progress,
            heroes = heroes,
            gears = gears,
            jewelries = jewelries,
            buildings = buildings,
            vehicles = vehicles,
            vehicleParts = vehicleParts,
            playerMaps = playerMaps,
            currentMap = currentMap,
            missions = missions,
            ongoingBattle = ongoingBattle)
    }

    private fun getHash(name: String, password: String): String {
        return Hashing.sha256().hashString("$name$pwSalt1$password$pwSalt2", StandardCharsets.UTF_8).toString()
    }


}
