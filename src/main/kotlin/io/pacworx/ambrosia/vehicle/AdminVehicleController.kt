package io.pacworx.ambrosia.vehicle

import io.pacworx.ambrosia.exceptions.GeneralException
import io.pacworx.ambrosia.player.AuditLogService
import io.pacworx.ambrosia.player.Player
import org.springframework.web.bind.annotation.*
import javax.transaction.Transactional
import javax.validation.Valid

@RestController
@CrossOrigin(maxAge = 3600)
@RequestMapping("admin/vehicle")
class AdminVehicleController(private val vehicleBaseRepository: VehicleBaseRepository,
                             private val auditLogService: AuditLogService) {

    @PostMapping
    @Transactional
    fun saveVehicle(@ModelAttribute("player") player: Player,
                    @RequestBody @Valid vehicle: VehicleBase): VehicleBase {
        if (vehicle.specialPart1 != null) {
            if (vehicle.specialPart1.slot != VehicleSlot.SPECIAL || vehicle.specialPart1Quality == null) {
                throw GeneralException(player, "Invalid vehicle", "Invalid special part 1")
            }
        } else { vehicle.specialPart1Quality = null }
        if (vehicle.specialPart2 != null) {
            if (vehicle.specialPart2.slot != VehicleSlot.SPECIAL || vehicle.specialPart2Quality == null) {
                throw GeneralException(player, "Invalid vehicle", "Invalid special part 2")
            }
        } else { vehicle.specialPart2Quality = null }
        if (vehicle.specialPart3 != null) {
            if (vehicle.specialPart3.slot != VehicleSlot.SPECIAL || vehicle.specialPart3Quality == null) {
                throw GeneralException(player, "Invalid vehicle", "Invalid special part 3")
            }
        } else { vehicle.specialPart3Quality = null }
        return vehicleBaseRepository.save(vehicle).also {
            auditLogService.log(player, "Saved vehicle ${it.name} #${it.id}")
        }
    }
}
