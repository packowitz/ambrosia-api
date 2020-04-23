package io.pacworx.ambrosia.upgrade

import io.pacworx.ambrosia.buildings.BuildingRepository
import io.pacworx.ambrosia.buildings.BuildingType
import io.pacworx.ambrosia.common.PlayerActionResponse
import io.pacworx.ambrosia.enums.JewelType
import io.pacworx.ambrosia.gear.GearService
import io.pacworx.ambrosia.gear.JewelryRepository
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
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.Instant
import java.time.temporal.ChronoUnit
import javax.transaction.Transactional

@RestController
@CrossOrigin(maxAge = 3600)
@RequestMapping("upgrade")
class UpgradeController(private val upgradeService: UpgradeService,
                        private val buildingRepository: BuildingRepository,
                        private val upgradeRepository: UpgradeRepository,
                        private val propertyService: PropertyService,
                        private val progressRepository: ProgressRepository,
                        private val resourcesService: ResourcesService,
                        private val vehicleRepository: VehicleRepository,
                        private val vehiclePartRepository: VehiclePartRepository,
                        private val jewelryRepository: JewelryRepository,
                        private val gearService: GearService) {

    @PostMapping("{upgradeId}/finish")
    @Transactional
    fun finishUpgrade(@ModelAttribute("player") player: Player,
                      @PathVariable upgradeId: Long): PlayerActionResponse {
        var currentUpgrades = upgradeService.getAllUpgrades(player)
        val upgrade = currentUpgrades.find { it.id == upgradeId }
            ?: throw RuntimeException("Unknown upgrade")
        if (!upgrade.isFinished()) {
            throw RuntimeException("The selected upgrade has not been finished")
        }
        upgradeRepository.delete(upgrade)
        currentUpgrades = currentUpgrades.filter { it.id != upgrade.id }
        currentUpgrades.filter { it.position > upgrade.position }.forEach { it.position -- }
        val building = upgrade.buildingType?.let { upgradeService.levelUpBuilding(player, it) }
        val vehicle = upgrade.vehicleId?.let { upgradeService.levelUpVehicle(it) }
        val vehiclePart = upgrade.vehiclePartId?.let { upgradeService.levelUpVehiclePart(it) }
        val jewelry = upgrade.jewelType?.let { jewelType ->
            jewelryRepository.findByPlayerIdAndType(player.id, jewelType)!!.also { it.increaseAmount(upgrade.jewelLevel!! + 1, 1) }
        }
        val gear = upgrade.gearModification?.let { gearService.modifyGear(it, upgrade.gearId!!) }
        return PlayerActionResponse(
            progress = progressRepository.getOne(player.id),
            resources = resourcesService.getResources(player),
            buildings = listOfNotNull(building),
            vehicles = listOfNotNull(vehicle),
            vehicleParts = listOfNotNull(vehiclePart),
            jewelries = listOfNotNull(jewelry),
            gears = listOfNotNull(gear),
            upgrades = currentUpgrades,
            upgradeRemoved = upgrade.id
        )
    }

    @PostMapping("{upgradeId}/cancel")
    @Transactional
    fun cancelUpgrade(@ModelAttribute("player") player: Player,
                      @PathVariable upgradeId: Long): PlayerActionResponse {
        var currentUpgrades = upgradeService.getAllUpgrades(player)
        val upgrade = currentUpgrades.find { it.id == upgradeId }
            ?: throw RuntimeException("Unknown upgrade")
        if (upgrade.isFinished()) {
            throw RuntimeException("You cannot cancel a finished upgrade")
        }

        var resources: Resources? = null
        upgrade.getResourcesAsCosts().forEach {
            resources = resourcesService.gainResources(player, it.type, it.amount)
        }
        val jewelry = upgrade.jewelType?.let { jewelType ->
            jewelryRepository.findByPlayerIdAndType(player.id, jewelType)!!.also { it.increaseAmount(upgrade.jewelLevel!!, 4) }
        }

        upgradeRepository.delete(upgrade)
        currentUpgrades = currentUpgrades.filter { it.id != upgrade.id }
        currentUpgrades.filter { it.position > upgrade.position }.forEach { it.position -- }
        return PlayerActionResponse(
            resources = resources,
            buildings = listOfNotNull(upgrade.buildingType?.let { upgradeService.cancelBuildingUpgrade(player, it) }),
            vehicles = listOfNotNull(upgrade.vehicleId?.let { upgradeService.cancelVehicleUpgrade(it) }),
            vehicleParts = listOfNotNull(upgrade.vehiclePartId?.let { upgradeService.cancelVehiclePartUpgrade(it) }),
            jewelries = listOfNotNull(jewelry),
            gears = listOfNotNull(upgrade.gearId?.let { upgradeService.cancelGearUpgrade(it) }),
            upgrades = currentUpgrades,
            upgradeRemoved = upgrade.id
        )
    }

    @PostMapping("{upgradeId}/moveup")
    @Transactional
    fun moveUpgradeUp(@ModelAttribute("player") player: Player,
                      @PathVariable upgradeId: Long): PlayerActionResponse {
        val currentUpgrades = upgradeService.getAllUpgrades(player)
        val upgrade = currentUpgrades.find { it.id == upgradeId }
            ?: throw RuntimeException("Unknown upgrade")
        val swapUpgrade = currentUpgrades.find { it.position == upgrade.position - 1 }
            ?: throw RuntimeException("There is no upgrade before")
        if (upgrade.isFinished() || swapUpgrade.isFinished()) {
            throw RuntimeException("You cannot move a finished upgrade")
        }

        val upgradeDuration = upgrade.getDuration()
        if (swapUpgrade.isInProgress()) {
            val now = Instant.now()
            swapUpgrade.secondsSpend = swapUpgrade.startTimestamp.until(now, ChronoUnit.SECONDS).toInt()
            upgrade.startTimestamp = now
        } else {
            upgrade.startTimestamp = swapUpgrade.startTimestamp
        }
        swapUpgrade.finishTimestamp = upgrade.finishTimestamp
        upgrade.finishTimestamp = upgrade.startTimestamp.plusSeconds(upgradeDuration)
        swapUpgrade.startTimestamp = upgrade.finishTimestamp
        upgrade.position--
        swapUpgrade.position++

        return PlayerActionResponse(
            upgrades = currentUpgrades.sortedBy { it.position }
        )
    }

    @PostMapping("check")
    @Transactional
    fun checkUpgrades(@ModelAttribute("player") player: Player): PlayerActionResponse {
        return PlayerActionResponse(upgrades = upgradeService.getAllUpgrades(player))
    }

    @PostMapping("building/{buildingType}")
    @Transactional
    fun upgradeBuilding(@ModelAttribute("player") player: Player,
                        @PathVariable buildingType: BuildingType): PlayerActionResponse {
        val building = buildingRepository.findByPlayerIdAndType(player.id, buildingType) ?:
            throw RuntimeException("Player ${player.id} hasn't discovered building ${buildingType.name} yet")
        if (building.upgradeTriggered) {
            throw RuntimeException("Upgrade for building $buildingType is already in progress")
        }
        val propTypeUpgradeTime = PropertyType.values()
            .filter { it.category == PropertyCategory.UPGRADE_TIME && it.buildingType == buildingType }
            .takeIf { it.size == 1 }
            ?.first()
            ?: throw RuntimeException("Found not exactly one property for upgrade time for $buildingType")
        val propTypeUpgradeCost = PropertyType.values()
            .filter { it.category == PropertyCategory.UPGRADE_COST && it.buildingType == buildingType }
            .takeIf { it.size == 1 }
            ?.first()
            ?: throw RuntimeException("Found not exactly one property for upgrade cost for $buildingType")
        val upgradeSeconds = propertyService.getProperties(propTypeUpgradeTime, building.level + 1)
            .takeIf { it.size == 1 }
            ?.first()?.value1?.toLong()
            ?: throw RuntimeException("Building $buildingType cannot be upgraded to higher than level ${building.level}")

        val resources = resourcesService.getResources(player)
        val upgrade = upgrade(player, resources, propTypeUpgradeCost, building.level + 1, upgradeSeconds, buildingType = buildingType)
        building.upgradeTriggered = true

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
            throw RuntimeException("Vehicle #$vehicleId not found")
        if (vehicle.playerId != player.id) {
            throw RuntimeException("You can only upgrade vehicles you own")
        }
        if (vehicle.upgradeTriggered) {
            throw RuntimeException("Upgrade for vehicle $vehicleId is already in progress")
        }
        if (vehicle.slot == null) {
            throw RuntimeException("You can only upgrade vehicles that are parked in a slot")
        }
        if (vehicle.missionId != null) {
            throw RuntimeException("You can only upgrade vehicles that are not on a mission")
        }
        if (vehicle.level >= vehicle.baseVehicle.maxLevel) {
            throw RuntimeException("Vehicle $vehicleId is already at max level")
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
            ?: throw RuntimeException("Vehicle $vehicleId cannot be upgraded to higher than level ${vehicle.level}")

        val resources = resourcesService.getResources(player)
        val upgrade = upgrade(player, resources, propTypeUpgradeCost, vehicle.level + 1, upgradeSeconds, vehicleId = vehicleId)
        vehicle.upgradeTriggered = true

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
            throw RuntimeException("Vehicle part #$vehiclePartId not found")
        if (vehiclePart.playerId != player.id) {
            throw RuntimeException("You can only upgrade vehicle parts you own")
        }
        if (vehiclePart.upgradeTriggered) {
            throw RuntimeException("Upgrade for vehicle part $vehiclePartId is already in progress")
        }
        if (vehiclePart.equippedTo != null) {
            throw RuntimeException("You can only upgrade vehicle parts that are not plugged into a vehicle")
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
            ?: throw RuntimeException("Vehicle part of quality ${vehiclePart.quality} cannot be upgraded to higher than level ${vehiclePart.level}")

        val resources = resourcesService.getResources(player)
        val upgrade = upgrade(player, resources, propTypeUpgradeCost, vehiclePart.level + 1, upgradeSeconds, vehiclePartId = vehiclePartId)
        vehiclePart.upgradeTriggered = true

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
            throw RuntimeException("You need to upgrade jewelry to be able to upgrade jewels of level $level")
        }
        val jewelry = jewelryRepository.findByPlayerIdAndType(player.id, jewelType)?.takeIf { it.getAmount(level) >= 4 }
            ?: throw RuntimeException("You don't own enough lvl $level $jewelType jewels to upgrade")

        val upgradeSeconds = propertyService.getProperties(PropertyType.JEWEL_UP_TIME, level + 1)
            .takeIf { it.size == 1 }
            ?.first()?.value1?.toLong()
            ?: throw RuntimeException("Jewels cannot be upgraded to higher than level $level")

        val resources = resourcesService.getResources(player)
        val upgrade = upgrade(player, resources, PropertyType.JEWEL_UP_COST, level + 1, upgradeSeconds, jewelType = jewelType)
        jewelry.increaseAmount(level, -4)

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
        val gear = gearService.getGear(gearId)
        val progress = progressRepository.getOne(player.id)
        if (!progress.modificationAllowed(gear.rarity, modification) || !gear.isModificationAllowed(modification)) {
            throw RuntimeException("Modification is not allowed on that gear")
        }

        val upgradeSeconds = propertyService.getProperties(modification.upTimeProp, gear.rarity.stars)
            .takeIf { it.size == 1 }
            ?.first()?.value1?.let { it * 100 / progress.gearModificationSpeed }?.toLong()
            ?: throw RuntimeException("Cannot find configuration for this modification")

        val resources = resourcesService.getResources(player)
        val upgrade = upgrade(player, resources, modification.upCostProp, gear.rarity.stars, upgradeSeconds, gearModification = modification, gearId = gear.id)
        gear.modificationInProgress = true

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
            throw RuntimeException("Upgrade queue is full. Cannot add another upgrade.")
        }

        val costs = propertyService.getProperties(costProp, toLevel)
            .map { Cost(it.value1, it.resourceType!!) }
        costs.forEach {
            resourcesService.spendResource(resources, it.type, it.amount)
        }

        val lastItem = currentUpgrades.maxBy { it.position }
        val startTime = lastItem?.finishTimestamp ?: Instant.now()
        val upgrade = Upgrade(
            playerId = player.id,
            position = (lastItem?.position ?: 0 ) + 1,
            startTimestamp = startTime,
            finishTimestamp = startTime.plusSeconds(seconds),
            buildingType = buildingType,
            vehicleId = vehicleId,
            vehiclePartId = vehiclePartId,
            jewelType = jewelType,
            jewelLevel = jewelType?.let { toLevel - 1 },
            gearModification = gearModification,
            gearId = gearId
        )
        upgrade.setResources(costs)
        return upgradeRepository.save(upgrade)
    }
}
