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

    fun activateVehicle(player: Player, vehicle: Vehicle, slot: Int? = null) {
        val garageSlots = progressRepository.getOne(player.id).garageSlots
        val vehicles = vehicleRepository.findAllByPlayerId(player.id).filter { it.slot != null }
        if (slot != null) {
            if (vehicles.any{ it.slot == slot }) {
                throw RuntimeException("Slot $slot is already occupied")
            }
            vehicle.slot = slot
        } else {
            (1..garageSlots).forEach { slotNr ->
                if (vehicles.none{ it.slot == slotNr }) {
                    vehicle.slot = slotNr
                    return
                }
            }
        }
    }

    fun plugInPart(vehicle: Vehicle, part: VehiclePart): VehiclePart? {
        when(part.type.slot) {
            VehicleSlot.ENGINE -> {
                if (part.quality.isHigherThan(vehicle.baseVehicle.engineQuality)) {
                    throw RuntimeException("Part Quality is too high")
                }
                val prevPart = vehicle.engine?.also { it.equippedTo = null }
                part.equippedTo = vehicle.id
                vehicle.engine = part
                return prevPart
            }
            VehicleSlot.FRAME -> {
                if (part.quality.isHigherThan(vehicle.baseVehicle.frameQuality)) {
                    throw RuntimeException("Part Quality is too high")
                }
                val prevPart = vehicle.frame?.also { it.equippedTo = null }
                part.equippedTo = vehicle.id
                vehicle.frame = part
                return prevPart
            }
            VehicleSlot.COMPUTER -> {
                if (part.quality.isHigherThan(vehicle.baseVehicle.computerQuality)) {
                    throw RuntimeException("Part Quality is too high")
                }
                val prevPart = vehicle.computer?.also { it.equippedTo = null }
                part.equippedTo = vehicle.id
                vehicle.computer = part
                return prevPart
            }
            VehicleSlot.SPECIAL -> {
                if (vehicle.specialPart1 == null && vehicle.baseVehicle.specialPart1Quality != null && !part.quality.isHigherThan(vehicle.baseVehicle.specialPart1Quality!!)) {
                    part.equippedTo = vehicle.id
                    vehicle.specialPart1 = part
                    return null
                }
                if (vehicle.specialPart2 == null && vehicle.baseVehicle.specialPart2Quality != null && !part.quality.isHigherThan(vehicle.baseVehicle.specialPart2Quality!!)) {
                    part.equippedTo = vehicle.id
                    vehicle.specialPart2 = part
                    return null
                }
                if (vehicle.specialPart3 == null && vehicle.baseVehicle.specialPart3Quality != null && !part.quality.isHigherThan(vehicle.baseVehicle.specialPart3Quality!!)) {
                    part.equippedTo = vehicle.id
                    vehicle.specialPart3 = part
                    return null
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

    fun getStat(vehicle: Vehicle?, stat: VehicleStat): Int {
        return vehicle?.getAllParts()?.sumBy { part ->
            PropertyType.values().find {
                it.partType == part.type && it.partQuality == part.quality
            }?.let { propType ->
                propertyService.getAllProperties(propType)
                    .filter { it.vehicleStat == stat }.sumBy { it.value1 }
            } ?: 0
        } ?: 0
    }
}
