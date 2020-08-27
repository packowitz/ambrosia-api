package io.pacworx.ambrosia.player

import com.google.common.hash.Hashing
import io.pacworx.ambrosia.achievements.AchievementService
import io.pacworx.ambrosia.achievements.Achievements
import io.pacworx.ambrosia.achievements.AchievementsRepository
import io.pacworx.ambrosia.battle.BattleService
import io.pacworx.ambrosia.battle.offline.MissionService
import io.pacworx.ambrosia.buildings.*
import io.pacworx.ambrosia.buildings.blackmarket.BlackMarketItemRepository
import io.pacworx.ambrosia.buildings.blackmarket.BlackMarketService
import io.pacworx.ambrosia.buildings.merchant.MerchantService
import io.pacworx.ambrosia.common.PlayerActionResponse
import io.pacworx.ambrosia.exceptions.EntityNotFoundException
import io.pacworx.ambrosia.exceptions.GeneralException
import io.pacworx.ambrosia.exceptions.UnauthorizedException
import io.pacworx.ambrosia.expedition.ExpeditionRepository
import io.pacworx.ambrosia.expedition.PlayerExpeditionRepository
import io.pacworx.ambrosia.gear.GearRepository
import io.pacworx.ambrosia.gear.JewelryRepository
import io.pacworx.ambrosia.hero.HeroRepository
import io.pacworx.ambrosia.hero.HeroService
import io.pacworx.ambrosia.inbox.InboxMessageRepository
import io.pacworx.ambrosia.maps.MapService
import io.pacworx.ambrosia.oddjobs.DailyActivity
import io.pacworx.ambrosia.oddjobs.DailyActivityRepository
import io.pacworx.ambrosia.oddjobs.OddJobService
import io.pacworx.ambrosia.progress.Progress
import io.pacworx.ambrosia.progress.ProgressRepository
import io.pacworx.ambrosia.properties.PropertyService
import io.pacworx.ambrosia.properties.PropertyType
import io.pacworx.ambrosia.resources.ResourceType
import io.pacworx.ambrosia.resources.Resources
import io.pacworx.ambrosia.resources.ResourcesRepository
import io.pacworx.ambrosia.resources.ResourcesService
import io.pacworx.ambrosia.story.StoryProgressRepository
import io.pacworx.ambrosia.upgrade.UpgradeService
import io.pacworx.ambrosia.vehicle.VehiclePartRepository
import io.pacworx.ambrosia.vehicle.VehicleRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.nio.charset.StandardCharsets
import java.time.Instant
import javax.transaction.Transactional

@Service
class PlayerService(
    private val playerRepository: PlayerRepository,
    private val progressRepository: ProgressRepository,
    private val heroService: HeroService,
    private val heroRepository: HeroRepository,
    private val gearRepository: GearRepository,
    private val jewelryRepository: JewelryRepository,
    private val battleService: BattleService,
    private val buildingRepository: BuildingRepository,
    private val mapService: MapService,
    private val propertyService: PropertyService,
    private val resourcesRepository: ResourcesRepository,
    private val resourcesService: ResourcesService,
    private val vehicleRepository: VehicleRepository,
    private val vehiclePartRepository: VehiclePartRepository,
    private val missionService: MissionService,
    private val upgradeService: UpgradeService,
    private val incubatorRepository: IncubatorRepository,
    private val storyProgressRepository: StoryProgressRepository,
    private val expeditionRepository: ExpeditionRepository,
    private val playerExpeditionRepository: PlayerExpeditionRepository,
    private val oddJobService: OddJobService,
    private val dailyActivityRepository: DailyActivityRepository,
    private val achievementsRepository: AchievementsRepository,
    private val merchantService: MerchantService,
    private val achievementService: AchievementService,
    private val blackMarketService: BlackMarketService,
    private val autoBreakdownConfigurationRepository: AutoBreakdownConfigurationRepository,
    private val inboxMessageRepository: InboxMessageRepository,
    private val auditLogService: AuditLogService
) {

    @Value("\${ambrosia.pw-salt-one}")
    private lateinit var pwSalt1: String

    @Value("\${ambrosia.pw-salt-two}")
    private lateinit var pwSalt2: String

    fun get(playerId: Long): Player? {
        return playerRepository.findByIdOrNull(playerId)
    }

    @Transactional(Transactional.TxType.REQUIRES_NEW)
    fun getAndLock(playerId: Long): Player? {
        return playerRepository.findByIdAndLockedIsNull(playerId)?.also { player ->
            player.locked = Instant.now()
        }
    }

    @Transactional(Transactional.TxType.REQUIRES_NEW)
    fun releaseLock(playerId: Long) {
        playerRepository.findByIdAndLockedIsNotNull(playerId)?.also { player ->
            player.locked = null
        }
    }

    fun signup(name: String, email: String, password: String, serviceAccount: Boolean = false): Player {
        val player = playerRepository.save(Player(
            name = name,
            email = email,
            password = if (serviceAccount) {
                password
            } else {
                getHash(name, password)
            },
            admin = serviceAccount,
            serviceAccount = serviceAccount,
            betaTester = serviceAccount
        ))
        resourcesRepository.save(Resources(
            playerId = player.id,
            steam = getStartingAmount(ResourceType.STEAM),
            steamMax = getStorageMax(ResourceType.STEAM_MAX),
            premiumSteam = getStartingAmount(ResourceType.PREMIUM_STEAM),
            premiumSteamMax = getStorageMax(ResourceType.PREMIUM_STEAM_MAX),
            cogwheels = getStartingAmount(ResourceType.COGWHEELS),
            cogwheelsMax = getStorageMax(ResourceType.COGWHEELS_MAX),
            premiumCogwheels = getStartingAmount(ResourceType.PREMIUM_COGWHEELS),
            premiumCogwheelsMax = getStorageMax(ResourceType.PREMIUM_COGWHEELS_MAX),
            tokens = getStartingAmount(ResourceType.TOKENS),
            tokensMax = getStorageMax(ResourceType.TOKENS_MAX),
            premiumTokens = getStartingAmount(ResourceType.PREMIUM_TOKENS),
            premiumTokensMax = getStorageMax(ResourceType.PREMIUM_TOKENS_MAX),
            coins = getStartingAmount(ResourceType.COINS),
            rubies = getStartingAmount(ResourceType.RUBIES),
            metal = getStartingAmount(ResourceType.METAL),
            metalMax = getStorageMax(ResourceType.METAL_MAX),
            iron = getStartingAmount(ResourceType.IRON),
            ironMax = getStorageMax(ResourceType.IRON_MAX),
            steel = getStartingAmount(ResourceType.STEEL),
            steelMax = getStorageMax(ResourceType.STEEL_MAX),
            wood = getStartingAmount(ResourceType.WOOD),
            woodMax = getStorageMax(ResourceType.WOOD_MAX),
            brownCoal = getStartingAmount(ResourceType.BROWN_COAL),
            brownCoalMax = getStorageMax(ResourceType.BROWN_COAL_MAX),
            blackCoal = getStartingAmount(ResourceType.BLACK_COAL),
            blackCoalMax = getStorageMax(ResourceType.BLACK_COAL_MAX),
            simpleGenome = getStartingAmount(ResourceType.SIMPLE_GENOME),
            commonGenome = getStartingAmount(ResourceType.COMMON_GENOME),
            uncommonGenome = getStartingAmount(ResourceType.UNCOMMON_GENOME),
            rareGenome = getStartingAmount(ResourceType.RARE_GENOME),
            epicGenome = getStartingAmount(ResourceType.EPIC_GENOME)
        ))

        val barracks = buildingRepository.save(Building(playerId = player.id, type = BuildingType.BARRACKS))
        buildingRepository.save(Building(playerId = player.id, type = BuildingType.STORAGE))

        val progress = Progress(
            playerId = player.id,
            maxXp = getStartingAmount(PropertyType.XP_MAX_PLAYER),
            vipMaxPoints = getStartingAmount(PropertyType.VIP_MAX_PLAYER, 0)
        )
        upgradeService.applyBuildingLevel(player, barracks, progress)
        progressRepository.save(progress)
        autoBreakdownConfigurationRepository.save(AutoBreakdownConfiguration(player.id))
        dailyActivityRepository.save(DailyActivity(playerId = player.id))
        achievementsRepository.save(Achievements(player.id))

        return player
    }

    fun getStartingAmount(propertyType: PropertyType, level: Int = 1): Int {
        return propertyService.getProperties(propertyType, level).first().value1
    }

    fun getStartingAmount(type: ResourceType): Int {
        return propertyService.getProperties(PropertyType.PLAYER_LVL_RESOURCES, 1)
            .find { p -> p.resourceType == type }?.value1 ?: 0
    }

    fun getStorageMax(type: ResourceType): Int {
        return propertyService.getProperties(PropertyType.STORAGE_BUILDING, 1)
            .find { it.resourceType == type }?.value1 ?: 0
    }

    fun login(email: String, password: String): Player {
        val player = playerRepository.findByEmailIgnoreCase(email.trim())
        if (player == null) {
            auditLogService.logError(0, "Login failed. No user with email $email")
            throw RuntimeException("Unknown email address")
        }
        if (getHash(player.name, password) != player.password) {
            auditLogService.logError(player.id, "Login failed. Wrong password.")
            throw UnauthorizedException(player, "Login failed. Wrong password.")
        }
        return player
    }

    fun save(player: Player): Player {
        return playerRepository.save(player)
    }

    fun response(player: Player, token: String? = null): PlayerActionResponse {
        player.didLogin()
        val upgrades = upgradeService.getAllUpgrades(player)
        val progress = progressRepository.getOne(player.id)
        val achievements = achievementsRepository.getOne(player.id)
        val resources = resourcesService.getResources(player)
        val heroes = heroRepository.findAllByPlayerIdOrderByLevelDescStarsDescHeroBase_IdAscIdAsc(player.id)
            .map { heroService.asHeroDto(it) }
        val gears = gearRepository.findAllByPlayerIdAndEquippedToIsNull(player.id)
        val jewelries = jewelryRepository.findAllByPlayerId(player.id)
        val buildings = buildingRepository.findAllByPlayerId(player.id)
        val vehicles = vehicleRepository.findAllByPlayerId(player.id)
        val vehicleParts = vehiclePartRepository.findAllByPlayerIdAndEquippedToIsNull(player.id)
        val playerMaps = mapService.getPlayerMaps(player)
        val currentMap = mapService.getCurrentPlayerMap(player, progress)
        val missions = missionService.getAllMissions(player)
        val dnaCubes = incubatorRepository.findAllByPlayerIdOrderByStartTimestamp(player.id)
        val expeditions = expeditionRepository.findAllByExpeditionBase_LevelAndActiveIsTrue(progress.expeditionLevel)
        val playerExpeditions = playerExpeditionRepository.findAllByPlayerIdOrderByStartTimestamp(player.id)
        val oddJobs = oddJobService.getOddJobs(player)
        val ongoingBattle = battleService.getOngoingBattle(player)
        val knownStories = storyProgressRepository.findAllByPlayerId(player.id).map { it.trigger.name }
        val dailyActivity = dailyActivityRepository.getOne(player.id).also { it.checkForReset() }
        val merchantItems = merchantService.getItems(player, progress)
        val blackMarketItems = blackMarketService.getPurchasableItems(player)
        val achievementRewards = achievementService.getActiveAchievementRewards(player)
        val autoBreakdownConfiguration = autoBreakdownConfigurationRepository.getOne(player.id)
        val inboxMessages = inboxMessageRepository.findAllByPlayerIdOrderByValidTimestamp(player.id)
        return PlayerActionResponse(
            resources = resources,
            token = token,
            player = save(player),
            progress = progress,
            achievements = achievements,
            heroes = heroes,
            gears = gears,
            jewelries = jewelries,
            buildings = buildings,
            vehicles = vehicles,
            vehicleParts = vehicleParts,
            playerMaps = playerMaps,
            currentMap = currentMap,
            missions = missions,
            incubators = dnaCubes,
            expeditions = expeditions,
            playerExpeditions = playerExpeditions,
            oddJobs = oddJobs,
            ongoingBattle = ongoingBattle,
            upgrades = upgrades,
            knownStories = knownStories,
            dailyActivity = dailyActivity,
            merchantItems = merchantItems,
            blackMarketItems = blackMarketItems,
            achievementRewards = achievementRewards,
            autoBreakdownConfiguration = autoBreakdownConfiguration,
            inboxMessages = inboxMessages
        )
    }

    private fun getHash(name: String, password: String): String {
        return Hashing.sha256().hashString("$name$pwSalt1$password$pwSalt2", StandardCharsets.UTF_8).toString()
    }


}
