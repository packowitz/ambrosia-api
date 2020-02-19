package io.pacworx.ambrosia.vehicle

import io.pacworx.ambrosia.player.Player
import org.springframework.stereotype.Service

@Service
class VehicleService(private val vehicleRepository: VehicleRepository,
                     private val vehicleBaseRepository: VehicleBaseRepository,
                     private val vehiclePartRepository: VehiclePartRepository) {

    fun gainVehicle(player: Player, vehicleBaseId: Long): Vehicle {
        val baseVehicle = vehicleBaseRepository.getOne(vehicleBaseId)
        return vehicleRepository.save(Vehicle(
            playerId = player.id,
            baseVehicle = baseVehicle
        ))
    }

    fun gainVehiclePart(player: Player, partType: PartType, partQuality: PartQuality): VehiclePart {
        return vehiclePartRepository.save(VehiclePart(
            playerId = player.id,
            type = partType,
            quality = partQuality
        ))
    }
}