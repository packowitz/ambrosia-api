package io.pacworx.ambrosia.upgrade

import io.pacworx.ambrosia.buildings.Building
import io.pacworx.ambrosia.buildings.BuildingRepository
import io.pacworx.ambrosia.buildings.BuildingType
import io.pacworx.ambrosia.player.Player
import io.pacworx.ambrosia.progress.ProgressRepository
import io.pacworx.ambrosia.properties.PropertyService
import io.pacworx.ambrosia.properties.PropertyType
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
                     private val progressRepository: ProgressRepository) {

    fun getAllUpgrades(player: Player): List<Upgrade> = upgradeRepository.findAllByPlayerIdOrderByPositionAsc(player.id)

    fun levelUpBuilding(player: Player, buildingType: BuildingType): Building {
        val building = buildingRepository.findByPlayerIdAndType(player.id, buildingType)!!.also {
            it.level++
            it.upgradeTriggered = false
        }
        when(building.type) {
            BuildingType.ACADEMY -> TODO()
            BuildingType.ARENA -> TODO()
            BuildingType.BARRACKS -> {
                val inc = propertyService.getProperties(PropertyType.BARRACKS_BUILDING, building.level).sumBy { it.value1 }
                progressRepository.getOne(player.id).barrackSize += inc
            }
            BuildingType.BAZAAR -> TODO()
            BuildingType.FORGE -> TODO()
            BuildingType.GARAGE -> TODO()
            BuildingType.JEWELRY -> TODO()
            BuildingType.LABORATORY -> TODO()
            BuildingType.STORAGE -> TODO()
        }
        return building
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

}