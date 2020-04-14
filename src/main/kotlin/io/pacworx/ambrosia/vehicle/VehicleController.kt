package io.pacworx.ambrosia.vehicle

import io.pacworx.ambrosia.common.PlayerActionResponse
import io.pacworx.ambrosia.player.Player
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@CrossOrigin(maxAge = 3600)
@RequestMapping("vehicle")
class VehicleController(
    private val vehicleRepository: VehicleRepository,
    private val vehiclePartRepository: VehiclePartRepository,
    private val vehicleService: VehicleService
) {

    @PostMapping("{vehicleId}/activate/{slot}")
    @Transactional
    fun activateVehicle(@ModelAttribute("player") player: Player,
                        @PathVariable vehicleId: Long,
                        @PathVariable slot: Int): PlayerActionResponse {
        val vehicle = vehicleRepository.getOne(vehicleId)
        if (vehicle.playerId != player.id) {
            throw RuntimeException("You don't own vehicle $vehicleId")
        }
        if (vehicle.slot != null) {
            throw RuntimeException("Vehicle $vehicleId is already activated")
        }
        vehicleService.activateVehicle(player, vehicle, slot)
        return PlayerActionResponse(vehicles = listOf(vehicle))
    }

    @PostMapping("{vehicleId}/deactivate")
    @Transactional
    fun deactivateVehicle(@ModelAttribute("player") player: Player, @PathVariable vehicleId: Long): PlayerActionResponse {
        val vehicle = vehicleRepository.getOne(vehicleId)
        if (vehicle.playerId != player.id) {
            throw RuntimeException("You don't own vehicle $vehicleId")
        }
        if (vehicle.missionId != null) {
            throw RuntimeException("You cannot deactivate a vehicle which is on a mission")
        }
        if (vehicle.upgradeTriggered) {
            throw RuntimeException("You cannot deactivate a vehicle which is currently upgrading")
        }
        vehicle.slot = null
        return PlayerActionResponse(vehicles = listOf(vehicle))
    }

    @PostMapping("{vehicleId}/plugin/{partId}")
    @Transactional
    fun pluginPart(@ModelAttribute("player") player: Player,
                   @PathVariable vehicleId: Long,
                   @PathVariable partId: Long): PlayerActionResponse {
        val vehicle = vehicleRepository.getOne(vehicleId)
        if (vehicle.playerId != player.id) {
            throw RuntimeException("You don't own vehicle $vehicleId")
        }
        val part = vehiclePartRepository.getOne(partId)
        if (part.playerId != player.id) {
            throw RuntimeException("You don't own part $partId")
        }
        if (vehicle.upgradeTriggered) {
            throw RuntimeException("You cannot plugin a part while it is upgrading")
        }
        if (vehicle.missionId != null) {
            throw RuntimeException("You cannot plugin a part to a vehicle which is on a mission")
        }
        if (part.level > vehicle.level) {
            throw RuntimeException("Part level too high. You need to level up the vehicle to plugin that part.")
        }
        val prevPart = vehicleService.plugInPart(vehicle, part)
        return PlayerActionResponse(vehicles = listOf(vehicle), vehicleParts = listOfNotNull(part, prevPart))
    }


    @PostMapping("{vehicleId}/unplug/{partId}")
    @Transactional
    fun unplugPart(@ModelAttribute("player") player: Player,
                   @PathVariable vehicleId: Long,
                   @PathVariable partId: Long): PlayerActionResponse {
        val vehicle = vehicleRepository.getOne(vehicleId)
        if (vehicle.playerId != player.id) {
            throw RuntimeException("You don't own vehicle $vehicleId")
        }
        val part = vehiclePartRepository.getOne(partId)
        if (part.playerId != player.id) {
            throw RuntimeException("You don't own part $partId")
        }
        if (vehicle.missionId != null) {
            throw RuntimeException("You cannot unplug a part from a vehicle which is on a mission")
        }
        if (part.equippedTo != vehicle.id) {
            throw RuntimeException("Part is not plugged in to selected vehicle")
        }
        vehicleService.unplugPart(vehicle, part)
        return PlayerActionResponse(vehicles = listOf(vehicle), vehicleParts = listOf(part))
    }
}
