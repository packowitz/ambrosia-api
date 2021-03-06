package io.pacworx.ambrosia.upgrade

import io.pacworx.ambrosia.achievements.AchievementsRepository
import io.pacworx.ambrosia.buildings.BuildingRepository
import io.pacworx.ambrosia.buildings.BuildingType
import io.pacworx.ambrosia.common.PlayerActionResponse
import io.pacworx.ambrosia.common.procs
import io.pacworx.ambrosia.exceptions.ConfigurationException
import io.pacworx.ambrosia.exceptions.EntityNotFoundException
import io.pacworx.ambrosia.exceptions.GeneralException
import io.pacworx.ambrosia.exceptions.InsufficientResourcesException
import io.pacworx.ambrosia.exceptions.UnauthorizedException
import io.pacworx.ambrosia.exceptions.VehicleBusyException
import io.pacworx.ambrosia.gear.GearService
import io.pacworx.ambrosia.gear.JewelType
import io.pacworx.ambrosia.gear.JewelryRepository
import io.pacworx.ambrosia.inbox.InboxMessageRepository
import io.pacworx.ambrosia.loot.LootItemType
import io.pacworx.ambrosia.loot.Looted
import io.pacworx.ambrosia.loot.LootedItem
import io.pacworx.ambrosia.loot.LootedType
import io.pacworx.ambrosia.oddjobs.OddJobService
import io.pacworx.ambrosia.player.AuditLogService
import io.pacworx.ambrosia.player.Player
import io.pacworx.ambrosia.progress.ProgressRepository
import io.pacworx.ambrosia.properties.PropertyCategory
import io.pacworx.ambrosia.properties.PropertyService
import io.pacworx.ambrosia.properties.PropertyType
import io.pacworx.ambrosia.resources.Resources
import io.pacworx.ambrosia.resources.ResourcesService
import io.pacworx.ambrosia.vehicle.PartQuality
import io.pacworx.ambrosia.vehicle.VehiclePartRepository
import io.pacworx.ambrosia.vehicle.VehicleRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.web.bind.annotation.*
import java.time.Instant
import java.time.LocalDateTime
import javax.transaction.Transactional

@RestController
@CrossOrigin(maxAge = 3600)
@RequestMapping("upgrade")
class UpgradeController(
    private val upgradeService: UpgradeService,
    private val buildingRepository: BuildingRepository,
    private val upgradeRepository: UpgradeRepository,
    private val propertyService: PropertyService,
    private val progressRepository: ProgressRepository,
    private val resourcesService: ResourcesService,
    private val vehicleRepository: VehicleRepository,
    private val vehiclePartRepository: VehiclePartRepository,
    private val jewelryRepository: JewelryRepository,
    private val gearService: GearService,
    private val auditLogService: AuditLogService,
    private val oddJobService: OddJobService,
    private val achievementsRepository: AchievementsRepository,
    private val inboxMessageRepository: InboxMessageRepository
) {

    @PostMapping("{upgradeId}/finish")
    @Transactional
    fun finishUpgrade(@ModelAttribute("player") player: Player,
                      @PathVariable upgradeId: Long): PlayerActionResponse {
        var currentUpgrades = upgradeService.getAllUpgrades(player)
        val upgrade = currentUpgrades.find { it.id == upgradeId }
            ?: throw EntityNotFoundException(player, "upgrade", upgradeId)
        if (!upgrade.isFinished()) {
            throw GeneralException(player, "Cannot finish upgrade", "Ugrade has not been finished")
        }
        val oddJobsEffected = oddJobService.upgradeFinished(player, upgrade)
        val achievements = achievementsRepository.getOne(player.id)
        upgrade.getResourcesAsCosts().forEach { achievements.resourceSpend(it.type, it.amount) }
        upgradeRepository.delete(upgrade)
        currentUpgrades = currentUpgrades.filter { it.id != upgrade.id }
        currentUpgrades.filter { it.position > upgrade.position }.forEach { it.position -- }
        var looted: Looted? = null
        val building = upgrade.buildingType?.let { upgradeService.levelUpBuilding(player, it, achievements) }
            ?.also { auditLogService.log(player, "Finish ${it.type.name} upgrade to level ${it.level}") }
        val vehicle = upgrade.vehicleId?.let { upgradeService.levelUpVehicle(it) }
            ?.also {
                achievements.vehiclesUpgradesDone ++
                looted = Looted(LootedType.UPGRADE, listOf(LootedItem(LootItemType.VEHICLE, value = it.id)))
                auditLogService.log(player, "Finish ${it.baseVehicle.name} #${it.id} upgrade to level ${it.level}")
            }
        val vehiclePart = upgrade.vehiclePartId?.let {upgradeService.levelUpVehiclePart(it) }
            ?.also {
                achievements.vehiclePartUpgradesDone ++
                looted = Looted(LootedType.UPGRADE, listOf(LootedItem(LootItemType.VEHICLE_PART, value = it.id)))
                auditLogService.log(player, "Finish ${it.quality.name} ${it.type.name} #${it.id} upgrade to level ${it.level}")
            }
        var jewelry = upgrade.jewelType?.let { jewelType ->
            jewelryRepository.findByPlayerIdAndType(player.id, jewelType)!!.also {
                val doubleJewel = procs(progressRepository.getOne(player.id).jewelMergeDoubleChance)
                it.increaseAmount(upgrade.jewelLevel!! + 1, if (doubleJewel) { 2 } else { 1 })
                achievements.jewelsMerged ++
                val lootedItem = LootedItem(LootItemType.JEWEL, jewelType = upgrade.jewelType, value = (upgrade.jewelLevel + 1).toLong())
                looted = Looted(LootedType.UPGRADE, listOfNotNull(
                    lootedItem,
                    if (doubleJewel) { lootedItem } else { null }
                ))
                auditLogService.log(player, "Finish ${it.type.name} jewel upgrade to level ${upgrade.jewelLevel + 1}")
            }
        }
        val gear = upgrade.gearModification?.let { mod ->
            gearService.modifyGear(player, mod, upgrade.gearId!!).also { gear ->
                when (mod) {
                    Modification.REROLL_JEWEL_1 -> 1
                    Modification.REROLL_JEWEL_2 -> 2
                    Modification.REROLL_JEWEL_3 -> 3
                    Modification.REROLL_JEWEL_4 -> 4
                    else -> null
                }?.let { jewelSlotNr ->
                    gear.getJewel(jewelSlotNr)?.let { jewel ->
                        jewelry = jewelryRepository.findByPlayerIdAndType(player.id, jewel.first)!!.also { jewelry ->
                            jewelry.increaseAmount(jewel.second, 1)
                        }
                    }
                }

                achievements.gearModified ++
                looted = Looted(LootedType.UPGRADE, listOf(LootedItem(LootItemType.GEAR, value = gear.id)))
                auditLogService.log(player, "Finish gear modification on gear #${gear.id}")
            }
        }

        return PlayerActionResponse(
            progress = progressRepository.getOne(player.id),
            achievements = achievements,
            resources = resourcesService.getResources(player),
            buildings = listOfNotNull(building),
            vehicles = listOfNotNull(vehicle),
            vehicleParts = listOfNotNull(vehiclePart),
            jewelries = listOfNotNull(jewelry),
            gears = listOfNotNull(gear),
            upgrades = currentUpgrades,
            upgradeRemoved = upgrade.id,
            oddJobs = oddJobsEffected.takeIf { it.isNotEmpty() },
            looted = looted
        )
    }

    @PostMapping("{upgradeId}/speedup")
    @Transactional
    fun speedup(@ModelAttribute("player") player: Player,
                @PathVariable upgradeId: Long): PlayerActionResponse {
        var currentUpgrades = upgradeService.getAllUpgrades(player)
        val upgrade = currentUpgrades.find { it.id == upgradeId }
            ?: throw EntityNotFoundException(player, "upgrade", upgradeId)
        if (!upgrade.isInProgress()) {
            throw GeneralException(player, "Cannot speedup upgrade", "Upgrade is not in progress")
        }
        val resources = resourcesService.getResources(player)
        resourcesService.spendRubies(resources, upgrade.speedup?.rubies ?: 0)
        upgrade.speedup = null
        val secondsSaved = upgrade.getSecondsUntilDone()
        upgrade.finishTimestamp = Instant.now().minusSeconds(5)
        currentUpgrades.filter { it.position > upgrade.position }.forEach {
            it.startTimestamp = it.startTimestamp.minusSeconds(secondsSaved)
            it.finishTimestamp = it.finishTimestamp.minusSeconds(secondsSaved)
            upgradeService.enrichSpeedup(it)
        }

        return PlayerActionResponse(
            resources = resources,
            upgrades = currentUpgrades
        )
    }

    @PostMapping("{upgradeId}/cancel")
    @Transactional
    fun cancelUpgrade(@ModelAttribute("player") player: Player,
                      @PathVariable upgradeId: Long): PlayerActionResponse {
        val timestamp = LocalDateTime.now()
        var currentUpgrades = upgradeService.getAllUpgrades(player)
        val upgrade = currentUpgrades.find { it.id == upgradeId }
            ?: throw EntityNotFoundException(player, "upgrade", upgradeId)
        val secondsLeft = upgrade.getSecondsUntilDone()
        if (upgrade.isFinished()) {
            throw GeneralException(player, "Cannot cancel upgrade", "You cannot cancel a finished upgrade")
        }

        var resources: Resources? = null
        upgrade.getResourcesAsCosts().forEach {
            resources = resourcesService.gainResources(player, it.type, it.amount)
        }
        val jewelry = upgrade.jewelType?.let { jewelType ->
            jewelryRepository.findByPlayerIdAndType(player.id, jewelType)!!.also { it.increaseAmount(upgrade.jewelLevel!!, 4) }
        }

        auditLogService.log(player, "Cancel upgrade #${upgrade.id} re gaining ${upgrade.getResourcesAsCosts().joinToString { "${it.amount} ${it.type.name}" }}")

        upgradeRepository.delete(upgrade)
        currentUpgrades = currentUpgrades.filter { it.id != upgrade.id }
        currentUpgrades.filter { it.position > upgrade.position }.forEach {
            it.position --
            it.startTimestamp = it.startTimestamp.minusSeconds(secondsLeft)
            it.finishTimestamp = it.finishTimestamp.minusSeconds(secondsLeft)
            upgradeService.enrichSpeedup(it)
        }
        return PlayerActionResponse(
            resources = resources,
            buildings = listOfNotNull(upgrade.buildingType?.let { upgradeService.cancelBuildingUpgrade(player, it) }),
            vehicles = listOfNotNull(upgrade.vehicleId?.let { upgradeService.cancelVehicleUpgrade(it) }),
            vehicleParts = listOfNotNull(upgrade.vehiclePartId?.let { upgradeService.cancelVehiclePartUpgrade(it) }),
            jewelries = listOfNotNull(jewelry),
            gears = listOfNotNull(upgrade.gearId?.let { upgradeService.cancelGearUpgrade(it) }),
            upgrades = currentUpgrades,
            upgradeRemoved = upgrade.id,
            inboxMessages = inboxMessageRepository.findAllByPlayerIdAndSendTimestampIsAfter(player.id, timestamp.minusSeconds(1))
        )
    }

    @PostMapping("{upgradeId}/moveup")
    @Transactional
    fun moveUpgradeUp(@ModelAttribute("player") player: Player,
                      @PathVariable upgradeId: Long): PlayerActionResponse {
        val currentUpgrades = upgradeService.getAllUpgrades(player)
        val upgrade = currentUpgrades.find { it.id == upgradeId }
            ?: throw EntityNotFoundException(player, "upgrade", upgradeId)
        val swapUpgrade = currentUpgrades.find { it.position == upgrade.position - 1 }
            ?: throw GeneralException(player, "Cannot move upgrade", "There is no upgrade before")
        if (upgrade.isFinished() || swapUpgrade.isFinished()) {
            throw GeneralException(player, "Cannot move upgrade", "You cannot move a finished upgrade")
        }

        val upgradeDuration = upgrade.getDuration()
        if (swapUpgrade.isInProgress()) {
            val now = Instant.now()
            upgrade.startTimestamp = now
        } else {
            upgrade.startTimestamp = swapUpgrade.startTimestamp
        }
        swapUpgrade.finishTimestamp = upgrade.finishTimestamp
        upgrade.finishTimestamp = upgrade.startTimestamp.plusSeconds(upgradeDuration)
        swapUpgrade.startTimestamp = upgrade.finishTimestamp
        upgrade.position--
        swapUpgrade.position++
        upgradeService.enrichSpeedup(upgrade)
        upgradeService.enrichSpeedup(swapUpgrade)

        auditLogService.log(player, "Move upgrade #${upgrade.id} up to position ${upgrade.position}. Upgrade #${swapUpgrade.id} is now on position ${swapUpgrade.position}")

        return PlayerActionResponse(
            upgrades = currentUpgrades.sortedBy { it.position }
        )
    }

    @GetMapping("check")
    @Transactional
    fun checkUpgrades(@ModelAttribute("player") player: Player): List<Upgrade> {
        return upgradeService.getAllUpgrades(player)
    }

    @PostMapping("building/{buildingType}")
    @Transactional
    fun upgradeBuilding(@ModelAttribute("player") player: Player,
                        @PathVariable buildingType: BuildingType): PlayerActionResponse {
        val building = buildingRepository.findByPlayerIdAndType(player.id, buildingType) ?:
            throw GeneralException(player, "Unknown building", "You haven't discovered building ${buildingType.name} yet")
        if (building.upgradeTriggered) {
            throw GeneralException(player, "Cannot upgrade building", "Upgrade for building $buildingType is already in progress")
        }
        val progress = progressRepository.getOne(player.id)
        val propTypeUpgradeTime = PropertyType.values()
            .filter { it.category == PropertyCategory.UPGRADE_TIME && it.buildingType == buildingType }
            .takeIf { it.size == 1 }
            ?.first()
            ?: throw ConfigurationException("Found not exactly one property for upgrade time for $buildingType")
        val propTypeUpgradeCost = PropertyType.values()
            .filter { it.category == PropertyCategory.UPGRADE_COST && it.buildingType == buildingType }
            .takeIf { it.size == 1 }
            ?.first()
            ?: throw ConfigurationException("Found not exactly one property for upgrade cost for $buildingType")
        val upgradeSeconds = propertyService.getProperties(propTypeUpgradeTime, building.level + 1)
            .takeIf { it.size == 1 }
            ?.first()?.let { (it.value1 * 100) / progress.builderSpeed }?.toLong()
            ?: throw ConfigurationException("Building $buildingType cannot be upgraded to higher than level ${building.level}")

        val resources = resourcesService.getResources(player)
        val upgrade = upgrade(player, resources, propTypeUpgradeCost, building.level + 1, upgradeSeconds, buildingType = buildingType)
        building.upgradeTriggered = true

        auditLogService.log(player, "Upgrade #${upgrade.id} triggered to upgrade ${building.type.name} to level ${building.level + 1} " +
                "spending ${upgrade.getResourcesAsCosts().joinToString { "${it.amount} ${it.type.name}" }}")

        return PlayerActionResponse(
            resources = resources,
            buildings = listOf(building),
            upgrades = listOf(upgrade)
        )
    }

    @PostMapping("vehicle/{vehicleId}")
    @Transactional
    fun upgradeVehicle(@ModelAttribute("player") player: Player,
                       @PathVariable vehicleId: Long): PlayerActionResponse {
        val vehicle = vehicleRepository.findByIdOrNull(vehicleId) ?:
            throw EntityNotFoundException(player, "vehicle", vehicleId)
        if (vehicle.playerId != player.id) {
            throw UnauthorizedException(player, "You can only upgrade vehicles you own")
        }
        if (!vehicle.isAvailable()) {
            throw VehicleBusyException(player, vehicle)
        }
        if (vehicle.level >= vehicle.baseVehicle.maxLevel) {
            throw GeneralException(player, "Cannot upgrade vehicle", "Vehicle $vehicleId is already at max level")
        }
        val progress = progressRepository.getOne(player.id)
        if (vehicle.level >= progress.vehicleUpgradeLevel) {
            throw GeneralException(player, "Cannot upgrade vehicle", "You need to upgrade garage to be able to upgrade vehicles of level ${vehicle.level}")
        }
        val propTypeUpgradeTime: PropertyType
        val propTypeUpgradeCost: PropertyType
        when {
            vehicle.specialPart3 != null -> {
                propTypeUpgradeTime = PropertyType.VEHICLE_3_UP_TIME
                propTypeUpgradeCost = PropertyType.VEHICLE_3_UP_COST
            }
            vehicle.specialPart2 != null -> {
                propTypeUpgradeTime = PropertyType.VEHICLE_2_UP_TIME
                propTypeUpgradeCost = PropertyType.VEHICLE_2_UP_COST
            }
            vehicle.specialPart1 != null -> {
                propTypeUpgradeTime = PropertyType.VEHICLE_1_UP_TIME
                propTypeUpgradeCost = PropertyType.VEHICLE_1_UP_COST
            }
            else -> {
                propTypeUpgradeTime = PropertyType.VEHICLE_0_UP_TIME
                propTypeUpgradeCost = PropertyType.VEHICLE_0_UP_COST
            }
        }
        val upgradeSeconds = propertyService.getProperties(propTypeUpgradeTime, vehicle.level + 1)
            .takeIf { it.size == 1 }
            ?.first()?.value1?.toLong()
            ?: throw GeneralException(player, "Cannot upgrade vehicle", "Vehicle $vehicleId cannot be upgraded to higher than level ${vehicle.level}")

        val resources = resourcesService.getResources(player)
        val upgrade = upgrade(player, resources, propTypeUpgradeCost, vehicle.level + 1, upgradeSeconds, vehicleId = vehicleId)
        vehicle.upgradeTriggered = true

        auditLogService.log(player, "Upgrade #${upgrade.id} triggered to upgrade vehicle ${vehicle.baseVehicle.name} #${vehicle.id} to level ${vehicle.level + 1} " +
                "spending ${upgrade.getResourcesAsCosts().joinToString { "${it.amount} ${it.type.name}" }}")

        return PlayerActionResponse(
            resources = resources,
            vehicles = listOf(vehicle),
            upgrades = listOf(upgrade)
        )
    }

    @PostMapping("vehicle_part/{vehiclePartId}")
    @Transactional
    fun upgradeVehiclePart(@ModelAttribute("player") player: Player,
                           @PathVariable vehiclePartId: Long): PlayerActionResponse {
        val vehiclePart = vehiclePartRepository.findByIdOrNull(vehiclePartId) ?:
            throw EntityNotFoundException(player, "vehicle part", vehiclePartId)
        if (vehiclePart.playerId != player.id) {
            throw UnauthorizedException(player, "You can only upgrade vehicle parts you own")
        }
        if (vehiclePart.upgradeTriggered) {
            throw GeneralException(player, "Cannot upgrade part", "Upgrade for vehicle part $vehiclePartId is already in progress")
        }
        val progress = progressRepository.getOne(player.id)
        if (vehiclePart.level >= progress.vehicleUpgradeLevel) {
            throw GeneralException(player, "Cannot upgrade part", "You need to upgrade garage to be able to upgrade parts of level ${vehiclePart.level}")
        }
        val propTypeUpgradeTime: PropertyType
        val propTypeUpgradeCost: PropertyType
        when (vehiclePart.quality) {
            PartQuality.BASIC -> {
                propTypeUpgradeTime = PropertyType.PART_BASIC_UP_TIME
                propTypeUpgradeCost = PropertyType.PART_BASIC_UP_COST
            }
            PartQuality.MODERATE -> {
                propTypeUpgradeTime = PropertyType.PART_MODERATE_UP_TIME
                propTypeUpgradeCost = PropertyType.PART_MODERATE_UP_COST
            }
            PartQuality.GOOD -> {
                propTypeUpgradeTime = PropertyType.PART_GOOD_UP_TIME
                propTypeUpgradeCost = PropertyType.PART_GOOD_UP_COST
            }
        }
        val upgradeSeconds = propertyService.getProperties(propTypeUpgradeTime, vehiclePart.level + 1)
            .takeIf { it.size == 1 }
            ?.first()?.value1?.toLong()
            ?: throw GeneralException(player, "Cannot upgrade part", "Vehicle part of quality ${vehiclePart.quality} cannot be upgraded to higher than level ${vehiclePart.level}")

        val resources = resourcesService.getResources(player)
        val upgrade = upgrade(player, resources, propTypeUpgradeCost, vehiclePart.level + 1, upgradeSeconds, vehiclePartId = vehiclePartId)
        vehiclePart.upgradeTriggered = true

        auditLogService.log(player, "Upgrade #${upgrade.id} triggered to upgrade vehicle part ${vehiclePart.quality.name} ${vehiclePart.type.name} to level ${vehiclePart.level + 1} " +
                "spending ${upgrade.getResourcesAsCosts().joinToString { "${it.amount} ${it.type.name}" }}")

        return PlayerActionResponse(
            resources = resources,
            vehicleParts = listOf(vehiclePart),
            upgrades = listOf(upgrade)
        )
    }

    @PostMapping("jewel/{jewelType}/{level}")
    @Transactional
    fun upgradeJewel(@ModelAttribute("player") player: Player,
                     @PathVariable jewelType: JewelType,
                     @PathVariable level: Int): PlayerActionResponse {
        val progress = progressRepository.getOne(player.id)
        if (level > progress.maxJewelUpgradingLevel) {
            throw GeneralException(player, "Cannot upgrade jewel", "You need to upgrade jewelry to be able to upgrade jewels of level $level")
        }
        val jewelry = jewelryRepository.findByPlayerIdAndType(player.id, jewelType)?.takeIf { it.getAmount(level) >= 4 }
            ?: throw InsufficientResourcesException(player.id, "$level $jewelType", 4)

        val upgradeSeconds = propertyService.getProperties(PropertyType.JEWEL_UP_TIME, level + 1)
            .takeIf { it.size == 1 }
            ?.first()?.value1?.toLong()
            ?: throw GeneralException(player, "Cannot upgrade jewel", "Jewels cannot be upgraded to higher than level $level")

        val resources = resourcesService.getResources(player)
        val upgrade = upgrade(player, resources, PropertyType.JEWEL_UP_COST, level + 1, upgradeSeconds, jewelType = jewelType)
        jewelry.increaseAmount(level, -4)

        auditLogService.log(player, "Upgrade #${upgrade.id} triggered to upgrade ${jewelType.name} jewel to level ${level} " +
                "spending ${upgrade.getResourcesAsCosts().joinToString { "${it.amount} ${it.type.name}" }}")

        return PlayerActionResponse(
            resources = resources,
            jewelries = listOf(jewelry),
            upgrades = listOf(upgrade)
        )
    }

    @PostMapping("gear/{gearId}/{modification}")
    @Transactional
    fun modifyGear(@ModelAttribute("player") player: Player,
                   @PathVariable gearId: Long,
                   @PathVariable modification: Modification
    ): PlayerActionResponse {
        val gear = gearService.getGear(player, gearId)
        val progress = progressRepository.getOne(player.id)
        if (!progress.modificationAllowed(gear.rarity, modification) || !gear.isModificationAllowed(modification)) {
            throw GeneralException(player, "Cannot modify gear", "Modification is not allowed on that gear")
        }

        val upgradeSeconds = propertyService.getProperties(modification.upTimeProp, gear.rarity.stars)
            .takeIf { it.size == 1 }
            ?.first()?.value1?.let { (it * 100) / progress.gearModificationSpeed }?.toLong()
            ?: throw ConfigurationException("Cannot find configuration for this modification")

        val resources = resourcesService.getResources(player)
        val upgrade = upgrade(player, resources, modification.upCostProp, gear.rarity.stars, upgradeSeconds, gearModification = modification, gearId = gear.id)
        gear.modificationInProgress = true

        auditLogService.log(player, "Upgrade #${upgrade.id} triggered to modify (${modification.name}) gear ${gear.rarity.stars}* ${gear.set.name} ${gear.type.name} #${gear.id} " +
                "spending ${upgrade.getResourcesAsCosts().joinToString { "${it.amount} ${it.type.name}" }}")

        return PlayerActionResponse(
            resources = resources,
            gears = listOf(gear),
            upgrades = listOf(upgrade)
        )
    }

    private fun upgrade(player: Player,
                        resources: Resources,
                        costProp: PropertyType,
                        toLevel: Int,
                        seconds: Long,
                        buildingType: BuildingType? = null,
                        vehicleId: Long? = null,
                        vehiclePartId: Long? = null,
                        jewelType: JewelType? = null,
                        gearModification: Modification? = null,
                        gearId: Long? = null): Upgrade {
        val progress = progressRepository.getOne(player.id)
        val currentUpgrades = upgradeService.getAllUpgrades(player)
        if (currentUpgrades.size >= progress.builderQueueLength) {
            throw GeneralException(player, "Cannot perform upgrade", "Upgrade queue is full. Cannot add another upgrade.")
        }

        val costs = propertyService.getProperties(costProp, toLevel)
            .map { Cost(it.value1, it.resourceType!!) }
        costs.forEach {
            resourcesService.spendResource(resources, it.type, it.amount)
        }

        val lastItem = currentUpgrades.maxBy { it.position }
        val startTime = lastItem?.takeIf { !it.isFinished() }?.finishTimestamp ?: Instant.now()
        val upgrade = Upgrade(
            playerId = player.id,
            position = (lastItem?.position ?: 0 ) + 1,
            startTimestamp = startTime,
            finishTimestamp = startTime.plusSeconds(seconds),
            origDuration = seconds,
            buildingType = buildingType,
            vehicleId = vehicleId,
            vehiclePartId = vehiclePartId,
            jewelType = jewelType,
            jewelLevel = jewelType?.let { toLevel - 1 },
            gearModification = gearModification,
            gearId = gearId
        )
        upgrade.setResources(costs)
        upgradeService.enrichSpeedup(upgrade)
        return upgradeRepository.save(upgrade)
    }
}
