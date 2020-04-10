package io.pacworx.ambrosia.upgrade

import io.pacworx.ambrosia.buildings.BuildingRepository
import io.pacworx.ambrosia.buildings.BuildingType
import io.pacworx.ambrosia.common.PlayerActionResponse
import io.pacworx.ambrosia.player.Player
import io.pacworx.ambrosia.progress.ProgressRepository
import io.pacworx.ambrosia.properties.PropertyCategory
import io.pacworx.ambrosia.properties.PropertyService
import io.pacworx.ambrosia.properties.PropertyType
import io.pacworx.ambrosia.resources.Resources
import io.pacworx.ambrosia.resources.ResourcesService
import org.springframework.web.bind.annotation.*
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
                        private val resourcesService: ResourcesService) {

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
        return PlayerActionResponse(
            progress = progressRepository.getOne(player.id),
            resources = resourcesService.getResources(player),
            buildings = listOfNotNull(building),
            vehicles = listOfNotNull(vehicle),
            vehicleParts = listOfNotNull(vehiclePart),
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

        upgradeRepository.delete(upgrade)
        currentUpgrades = currentUpgrades.filter { it.id != upgrade.id }
        currentUpgrades.filter { it.position > upgrade.position }.forEach { it.position -- }
        return PlayerActionResponse(
            resources = resources,
            buildings = listOfNotNull(upgrade.buildingType?.let { upgradeService.cancelBuildingUpgrade(player, it) }),
            vehicles = listOfNotNull(upgrade.vehicleId?.let { upgradeService.cancelVehicleUpgrade(it) }),
            vehicleParts = listOfNotNull(upgrade.vehiclePartId?.let { upgradeService.cancelVehiclePartUpgrade(it) }),
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
            throw RuntimeException("Update for building $buildingType is already in progress")
        }
        val propTypeUpgradeTime = PropertyType.values()
            .filter { it.category == PropertyCategory.BUILDING_UP_TIME && it.buildingType == buildingType }
            .takeIf { it.size == 1 }
            ?.first()
            ?: throw RuntimeException("Found not exactly one property for upgrade time for $buildingType")
        val propTypeUpgradeCost = PropertyType.values()
            .filter { it.category == PropertyCategory.BUILDING_UP_COST && it.buildingType == buildingType }
            .takeIf { it.size == 1 }
            ?.first()
            ?: throw RuntimeException("Found not exactly one property for upgrade cost for $buildingType")
        val upgradeSeconds = propertyService.getProperties(propTypeUpgradeTime, building.level + 1)
            .takeIf { it.size == 1 }
            ?.first()?.let { it.value1 }
            ?: throw RuntimeException("Building $buildingType cannot be upgraded to higher than level ${building.level}")
        val progress = progressRepository.getOne(player.id)
        val currentUpgrades = upgradeService.getAllUpgrades(player)
        if (currentUpgrades.size >= progress.builderQueueLength) {
            throw RuntimeException("Builder queue is full. Cannot add another upgrade.")
        }

        var resources = resourcesService.getResources(player)
        val costs = propertyService.getProperties(propTypeUpgradeCost, building.level + 1)
            .map { Cost(it.value1, it.resourceType!!) }
        costs.forEach {
            resources = resourcesService.spendResource(resources, it.type, it.amount)
        }

        val lastItem = currentUpgrades.maxBy { it.position }
        val startTime = lastItem?.finishTimestamp ?: Instant.now()
        val upgrade = Upgrade(
            playerId = player.id,
            position = (lastItem?.position ?: 0 ) + 1,
            startTimestamp = startTime,
            finishTimestamp = startTime.plusSeconds(upgradeSeconds.toLong()),
            buildingType = buildingType
        )
        upgrade.setResources(costs)
        upgradeRepository.save(upgrade)
        building.upgradeTriggered = true

        return PlayerActionResponse(
            resources = resources,
            buildings = listOf(building),
            upgrades = currentUpgrades + upgrade
        )
    }
}