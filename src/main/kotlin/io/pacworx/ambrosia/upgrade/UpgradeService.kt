package io.pacworx.ambrosia.upgrade

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
import io.pacworx.ambrosia.vehicle.Vehicle
import io.pacworx.ambrosia.vehicle.VehiclePart
import io.pacworx.ambrosia.vehicle.VehiclePartRepository
import io.pacworx.ambrosia.vehicle.VehicleRepository
import org.springframework.stereotype.Service

@Service
class UpgradeService(private val upgradeRepository: UpgradeRepository,
                     private val buildingRepository: BuildingRepository,
                     private val vehicleRepository: VehicleRepository,
                     private val vehiclePartRepository: VehiclePartRepository,
                     private val propertyService: PropertyService,
                     private val progressRepository: ProgressRepository,
                     private val resourcesService: ResourcesService,
                     private val gearRepository: GearRepository) {

    fun getAllUpgrades(player: Player): List<Upgrade> = upgradeRepository.findAllByPlayerIdOrderByPositionAsc(player.id)

    fun levelUpBuilding(player: Player, buildingType: BuildingType): Building {
        return buildingRepository.findByPlayerIdAndType(player.id, buildingType)!!.also {
            it.level++
            it.upgradeTriggered = false
            applyBuildingLevel(player, it)
        }
    }

    fun applyBuildingLevel(player: Player, building: Building, progress: Progress = progressRepository.getOne(player.id)) {
        when(building.type) {
            BuildingType.ACADEMY -> {
                progress.maxTrainingLevel = propertyService.getProperties(PropertyType.ACADEMY_BUILDING, building.level).sumBy { it.value1 }
            }
            BuildingType.ARENA -> TODO()
            BuildingType.BARRACKS -> {
                progress.barrackSize += propertyService.getProperties(PropertyType.BARRACKS_BUILDING, building.level).sumBy { it.value1 }
            }
            BuildingType.BAZAAR -> TODO()
            BuildingType.FORGE -> {
                propertyService.getProperties(PropertyType.FORGE_MOD_RARITY, building.level).forEach {
                    progress.gearModificationRarity = it.value1
                }
                propertyService.getProperties(PropertyType.FORGE_MOD_SPEED, building.level).forEach {
                    progress.gearModificationSpeed += it.value1
                }
                propertyService.getProperties(PropertyType.FORGE_BREAKDOWN_RARITY, building.level).forEach {
                    progress.gearBreakDownRarity = it.value1
                }
                propertyService.getProperties(PropertyType.FORGE_BREAKDOWN_RES, building.level).forEach {
                    progress.gearBreakDownResourcesInc += it.value1
                }
                propertyService.getProperties(PropertyType.FORGE_REROLL_QUAL, building.level).forEach {
                    progress.reRollGearQualityEnabled = progress.reRollGearQualityEnabled || it.value1 == 1
                }
                propertyService.getProperties(PropertyType.FORGE_REROLL_STAT, building.level).forEach {
                    progress.reRollGearStatEnabled = progress.reRollGearStatEnabled || it.value1 == 1
                }
                propertyService.getProperties(PropertyType.FORGE_INC_RARITY, building.level).forEach {
                    progress.incGearRarityEnabled = progress.incGearRarityEnabled || it.value1 == 1
                }
                propertyService.getProperties(PropertyType.FORGE_REROLL_JEWEL, building.level).forEach {
                    progress.reRollGearJewelEnabled = progress.reRollGearJewelEnabled || it.value1 == 1
                }
                propertyService.getProperties(PropertyType.FORGE_ADD_JEWEL, building.level).forEach {
                    progress.addGearJewelEnabled = progress.addGearJewelEnabled || it.value1 == 1
                }
                propertyService.getProperties(PropertyType.FORGE_ADD_SP_JEWEL, building.level).forEach {
                    progress.addGearSpecialJewelEnabled = progress.addGearSpecialJewelEnabled || it.value1 == 1
                }
            }
            BuildingType.GARAGE -> {
                propertyService.getProperties(PropertyType.GARAGE_BUILDING, building.level).forEach { prop ->
                    progress.vehicleStorage += prop.value1
                    prop.value2?.let { progress.vehiclePartStorage += it }
                }
            }
            BuildingType.JEWELRY -> {
                progress.maxJewelUpgradingLevel = propertyService.getProperties(PropertyType.JEWELRY_BUILDING, building.level).sumBy { it.value1 }
            }
            BuildingType.LABORATORY -> {
                propertyService.getProperties(PropertyType.LABORATORY_INCUBATORS, building.level).forEach { prop ->
                    progress.incubators += prop.value1
                }
                propertyService.getProperties(PropertyType.LABORATORY_SPEED, building.level).forEach { prop ->
                    progress.labSpeed += prop.value1
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
