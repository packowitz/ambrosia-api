package io.pacworx.ambrosia.vehicle

import io.pacworx.ambrosia.battle.Battle
import io.pacworx.ambrosia.player.Player
import io.pacworx.ambrosia.progress.ProgressRepository
import io.pacworx.ambrosia.properties.PropertyService
import io.pacworx.ambrosia.properties.PropertyType
import org.springframework.stereotype.Service

@Service
class VehicleService(private val vehicleRepository: VehicleRepository,
                     private val vehicleBaseRepository: VehicleBaseRepository,
                     private val vehiclePartRepository: VehiclePartRepository,
                     private val propertyService: PropertyService,
                     private val progressRepository: ProgressRepository) {

    fun gainVehicle(player: Player, vehicleBaseId: Long): Vehicle {
        val baseVehicle = vehicleBaseRepository.getOne(vehicleBaseId)
        val vehicle = Vehicle(
            playerId = player.id,
            baseVehicle = baseVehicle
        )
        activateVehicle(player, vehicle)
        return vehicleRepository.save(vehicle)
    }

    fun gainVehiclePart(player: Player, partType: PartType, partQuality: PartQuality): VehiclePart {
        return vehiclePartRepository.save(VehiclePart(
            playerId = player.id,
            type = partType,
            quality = partQuality
        ))
    }

    fun activateVehicle(player: Player, vehicle: Vehicle) {
        val garageSlots = progressRepository.getOne(player.id).garageSlots
        val vehicles = vehicleRepository.findAllByPlayerId(player.id).filter { it.slot != null }
        (1..garageSlots).forEach { slot ->
            if (vehicles.none{ it.slot == slot }) {
                vehicle.slot = slot
                return
            }
        }
    }

    fun applyPartsToBattle(vehicle: Vehicle, battle: Battle) {
        vehicle.getAllParts().forEach { part ->
            PropertyType.values().find {
                it.partType == part.type && it.partQuality == part.quality
            }?.let { propType ->
                propertyService.getAllProperties(propType).forEach { prop ->
                    prop.vehicleStat?.apply(battle, prop.value1)
                }
            }

        }
    }
}
