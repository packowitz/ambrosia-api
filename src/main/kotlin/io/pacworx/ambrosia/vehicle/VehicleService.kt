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

    fun plugInPart(vehicle: Vehicle, part: VehiclePart) {
        when(part.type.slot) {
            VehicleSlot.ENGINE -> {
                if (vehicle.engine != null) {
                    throw RuntimeException("Vehicle already has an engine plugged in")
                }
                if (part.quality.isHigherThan(vehicle.baseVehicle.engineQuality)) {
                    throw RuntimeException("Part Quality is too high")
                }
                part.equippedTo = vehicle.id
                vehicle.engine = part
            }
            VehicleSlot.FRAME -> {
                if (vehicle.frame != null) {
                    throw RuntimeException("Vehicle already has a frame plugged in")
                }

                if (part.quality.isHigherThan(vehicle.baseVehicle.frameQuality)) {
                    throw RuntimeException("Part Quality is too high")
                }
                part.equippedTo = vehicle.id
                vehicle.frame = part
            }
            VehicleSlot.COMPUTER -> {
                if (vehicle.computer != null) {
                    throw RuntimeException("Vehicle already has a computer plugged in")
                }
                if (part.quality.isHigherThan(vehicle.baseVehicle.computerQuality)) {
                    throw RuntimeException("Part Quality is too high")
                }
                part.equippedTo = vehicle.id
                vehicle.computer = part
            }
            VehicleSlot.SPECIAL -> {
                if (vehicle.specialPart1 == null && vehicle.baseVehicle.specialPart1Quality != null && !part.quality.isHigherThan(vehicle.baseVehicle.specialPart1Quality)) {
                    part.equippedTo = vehicle.id
                    vehicle.specialPart1 = part
                    return
                }
                if (vehicle.specialPart2 == null && vehicle.baseVehicle.specialPart2Quality != null && !part.quality.isHigherThan(vehicle.baseVehicle.specialPart2Quality)) {
                    part.equippedTo = vehicle.id
                    vehicle.specialPart2 = part
                    return
                }
                if (vehicle.specialPart3 == null && vehicle.baseVehicle.specialPart3Quality != null && !part.quality.isHigherThan(vehicle.baseVehicle.specialPart3Quality)) {
                    part.equippedTo = vehicle.id
                    vehicle.specialPart3 = part
                    return
                }
                throw RuntimeException("No valid spot for this special item")
            }
        }
    }

    fun unplugPart(vehicle: Vehicle, part: VehiclePart) {
        part.equippedTo = null
        if (vehicle.engine?.id == part.id) { vehicle.engine = null }
        if (vehicle.frame?.id == part.id) { vehicle.frame = null }
        if (vehicle.computer?.id == part.id) { vehicle.computer = null }
        if (vehicle.specialPart1?.id == part.id) { vehicle.specialPart1 = null }
        if (vehicle.specialPart2?.id == part.id) { vehicle.specialPart2 = null }
        if (vehicle.specialPart3?.id == part.id) { vehicle.specialPart3 = null }
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
