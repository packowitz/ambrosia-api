package io.pacworx.ambrosia.upgrade

import io.pacworx.ambrosia.achievements.Achievements
import io.pacworx.ambrosia.buildings.Building
import io.pacworx.ambrosia.buildings.BuildingRepository
import io.pacworx.ambrosia.buildings.BuildingType
import io.pacworx.ambrosia.gear.Gear
import io.pacworx.ambrosia.gear.GearRepository
import io.pacworx.ambrosia.player.Player
import io.pacworx.ambrosia.progress.Progress
import io.pacworx.ambrosia.progress.ProgressRepository
import io.pacworx.ambrosia.properties.PropertyService
import io.pacworx.ambrosia.properties.PropertyType
import io.pacworx.ambrosia.resources.ResourcesService
import io.pacworx.ambrosia.speedup.SpeedupService
import io.pacworx.ambrosia.speedup.SpeedupType
import io.pacworx.ambrosia.vehicle.Vehicle
import io.pacworx.ambrosia.vehicle.VehiclePart
import io.pacworx.ambrosia.vehicle.VehiclePartRepository
import io.pacworx.ambrosia.vehicle.VehicleRepository
import org.springframework.stereotype.Service

@Service
class UpgradeService(
    private val upgradeRepository: UpgradeRepository,
    private val buildingRepository: BuildingRepository,
    private val vehicleRepository: VehicleRepository,
    private val vehiclePartRepository: VehiclePartRepository,
    private val propertyService: PropertyService,
    private val progressRepository: ProgressRepository,
    private val resourcesService: ResourcesService,
    private val gearRepository: GearRepository,
    private val speedupService: SpeedupService
) {

    fun getAllUpgrades(player: Player): List<Upgrade> =
        upgradeRepository.findAllByPlayerIdOrderByPositionAsc(player.id).onEach { enrichSpeedup(it) }

    fun enrichSpeedup(upgrade: Upgrade) {
        if (upgrade.isInProgress()) {
            upgrade.speedup = speedupService.speedup(SpeedupType.UPGRADE, upgrade.getDuration(), upgrade.getSecondsUntilDone())
        } else {
            upgrade.speedup = null
        }
    }

    fun levelUpBuilding(player: Player, buildingType: BuildingType, achievements: Achievements): Building {
        val allBuildings = buildingRepository.findAllByPlayerId(player.id)
        val building = allBuildings.find { it.type == buildingType }!!
        building.let {
            it.level ++
            it.upgradeTriggered = false
            applyBuildingLevel(player, it)
        }
        achievements.buildingsUpgradesDone ++
        achievements.buildingMinLevel = allBuildings.filter { it.type.upgradeable }.minBy { it.level }!!.level
        return building
    }

    fun applyBuildingLevel(player: Player, building: Building, progress: Progress = progressRepository.getOne(player.id)) {
        when(building.type) {
            BuildingType.ACADEMY -> {
                propertyService.getProperties(PropertyType.ACADEMY_BUILDING, building.level).forEach { prop ->
                    prop.progressStat?.apply(progress, prop.value1)
                }
            }
            BuildingType.ARENA -> {
                // TODO
            }
            BuildingType.BARRACKS -> {
                propertyService.getProperties(PropertyType.BARRACKS_BUILDING, building.level).forEach { prop ->
                    prop.progressStat?.apply(progress, prop.value1)
                }
            }
            BuildingType.BAZAAR -> {
                propertyService.getProperties(PropertyType.BAZAAR_BUILDING, building.level).forEach { prop ->
                    prop.progressStat?.apply(progress, prop.value1)
                }
            }
            BuildingType.FORGE -> {
                propertyService.getProperties(PropertyType.FORGE_BUILDING, building.level).forEach { prop ->
                    prop.progressStat?.apply(progress, prop.value1)
                }
            }
            BuildingType.GARAGE -> {
                propertyService.getProperties(PropertyType.GARAGE_BUILDING, building.level).forEach { prop ->
                    prop.progressStat?.apply(progress, prop.value1)
                }
            }
            BuildingType.JEWELRY -> {
                propertyService.getProperties(PropertyType.JEWELRY_BUILDING, building.level).forEach { prop ->
                    prop.progressStat?.apply(progress, prop.value1)
                }
            }
            BuildingType.LABORATORY -> {
                propertyService.getProperties(PropertyType.LABORATORY_BUILDING, building.level).forEach { prop ->
                    prop.progressStat?.apply(progress, prop.value1)
                }
            }
            BuildingType.STORAGE -> {
                propertyService.getProperties(PropertyType.STORAGE_BUILDING, building.level)
                .filter { it.resourceType != null }
                    .forEach {
                        resourcesService.gainResources(player, it.resourceType!!, it.value1)
                    }
            }
        }
    }

    fun levelUpVehicle(vehicleId: Long): Vehicle {
        return vehicleRepository.getOne(vehicleId).also {
            it.level++
            it.upgradeTriggered = false
        }
    }

    fun levelUpVehiclePart(vehiclePartId: Long): VehiclePart {
        return vehiclePartRepository.getOne(vehiclePartId).also {
            it.level++
            it.upgradeTriggered = false
        }
    }

    fun cancelBuildingUpgrade(player: Player, buildingType: BuildingType): Building {
        return buildingRepository.findByPlayerIdAndType(player.id, buildingType)!!.also {
            it.upgradeTriggered = false
        }
    }

    fun cancelVehicleUpgrade(vehicleId: Long): Vehicle {
        return vehicleRepository.getOne(vehicleId).also {
            it.upgradeTriggered = false
        }
    }

    fun cancelVehiclePartUpgrade(vehiclePartId: Long): VehiclePart {
        return vehiclePartRepository.getOne(vehiclePartId).also {
            it.upgradeTriggered = false
        }
    }

    fun cancelGearUpgrade(gearId: Long): Gear {
        return gearRepository.getOne(gearId).also {
            it.modificationInProgress = false
        }
    }

}
