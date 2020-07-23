package io.pacworx.ambrosia.vehicle

import io.pacworx.ambrosia.common.PlayerActionResponse
import io.pacworx.ambrosia.exceptions.EntityNotFoundException
import io.pacworx.ambrosia.exceptions.GeneralException
import io.pacworx.ambrosia.exceptions.UnauthorizedException
import io.pacworx.ambrosia.exceptions.VehicleBusyException
import io.pacworx.ambrosia.player.AuditLogService
import io.pacworx.ambrosia.player.Player
import org.springframework.data.repository.findByIdOrNull
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.*

@RestController
@CrossOrigin(maxAge = 3600)
@RequestMapping("vehicle")
class VehicleController(
    private val vehicleRepository: VehicleRepository,
    private val vehiclePartRepository: VehiclePartRepository,
    private val vehicleBaseRepository: VehicleBaseRepository,
    private val vehicleService: VehicleService,
    private val auditLogService: AuditLogService
) {

    @GetMapping
    fun getVehicles(): List<VehicleBase> = vehicleBaseRepository.findAll()

    @PostMapping("{vehicleId}/activate/{slot}")
    @Transactional
    fun activateVehicle(@ModelAttribute("player") player: Player,
                        @PathVariable vehicleId: Long,
                        @PathVariable slot: Int): PlayerActionResponse {
        val vehicle = vehicleRepository.findByIdOrNull(vehicleId)
            ?: throw EntityNotFoundException(player, "vehicle", vehicleId)
        if (vehicle.playerId != player.id) {
            throw UnauthorizedException(player, "You don't own vehicle #$vehicleId")
        }
        if (vehicle.slot != null) {
            throw GeneralException(player, "Cannot activate vehicle", "Vehicle $vehicleId is already activated")
        }
        vehicleService.activateVehicle(player, vehicle, slot)
        auditLogService.log(player, "Activate vehicle ${vehicle.baseVehicle.name} #${vehicle.id} in slot ${vehicle.slot}")
        return PlayerActionResponse(vehicles = listOf(vehicle))
    }

    @PostMapping("{vehicleId}/deactivate")
    @Transactional
    fun deactivateVehicle(@ModelAttribute("player") player: Player, @PathVariable vehicleId: Long): PlayerActionResponse {
        val vehicle = vehicleRepository.findByIdOrNull(vehicleId)
            ?: throw EntityNotFoundException(player, "vehicle", vehicleId)
        if (vehicle.playerId != player.id) {
            throw UnauthorizedException(player, "You don't own vehicle #$vehicleId")
        }
        if (!vehicle.isAvailable()) {
            throw VehicleBusyException(player, vehicle)
        }
        vehicle.slot = null
        auditLogService.log(player, "Deactivate vehicle ${vehicle.baseVehicle.name} #${vehicle.id}")
        return PlayerActionResponse(vehicles = listOf(vehicle))
    }

    @PostMapping("{vehicleId}/plugin/{partId}")
    @Transactional
    fun pluginPart(@ModelAttribute("player") player: Player,
                   @PathVariable vehicleId: Long,
                   @PathVariable partId: Long): PlayerActionResponse {
        val vehicle = vehicleRepository.findByIdOrNull(vehicleId)
            ?: throw EntityNotFoundException(player, "vehicle", vehicleId)
        if (vehicle.playerId != player.id) {
            throw UnauthorizedException(player, "You don't own vehicle #$vehicleId")
        }
        val part = vehiclePartRepository.getOne(partId)
        if (part.playerId != player.id) {
            throw UnauthorizedException(player, "You don't own part $partId")
        }
        if (!vehicle.isAvailable()) {
            throw VehicleBusyException(player, vehicle)
        }
        if (part.level > vehicle.level) {
            throw GeneralException(player, "Cannot plug in part", "Part level too high. You need to level up the vehicle to plugin that part.")
        }
        val prevPart = vehicleService.plugInPart(player, vehicle, part)
        auditLogService.log(player, "Plug in part ${part.quality.name} ${part.type.name} to vehicle ${vehicle.baseVehicle.name} #${vehicle.id}")
        return PlayerActionResponse(vehicles = listOf(vehicle), vehicleParts = listOfNotNull(part, prevPart))
    }


    @PostMapping("{vehicleId}/unplug/{partId}")
    @Transactional
    fun unplugPart(@ModelAttribute("player") player: Player,
                   @PathVariable vehicleId: Long,
                   @PathVariable partId: Long): PlayerActionResponse {
        val vehicle = vehicleRepository.findByIdOrNull(vehicleId)
            ?: throw EntityNotFoundException(player, "vehicle", vehicleId)
        if (vehicle.playerId != player.id) {
            throw UnauthorizedException(player, "You don't own vehicle #$vehicleId")
        }
        val part = vehiclePartRepository.getOne(partId)
        if (part.playerId != player.id) {
            throw UnauthorizedException(player, "You don't own part $partId")
        }
        if (!vehicle.isAvailable()) {
            throw VehicleBusyException(player, vehicle)
        }
        if (part.equippedTo != vehicle.id) {
            throw GeneralException(player, "Cannot unplug part", "Part is not plugged in to selected vehicle")
        }
        vehicleService.unplugPart(vehicle, part)
        auditLogService.log(player, "Unplug part ${part.quality.name} ${part.type.name} from vehicle ${vehicle.baseVehicle.name} #${vehicle.id}")
        return PlayerActionResponse(vehicles = listOf(vehicle), vehicleParts = listOf(part))
    }
}
