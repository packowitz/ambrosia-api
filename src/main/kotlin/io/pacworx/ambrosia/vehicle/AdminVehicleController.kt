package io.pacworx.ambrosia.vehicle

import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.transaction.Transactional
import javax.validation.Valid

@RestController
@CrossOrigin(maxAge = 3600)
@RequestMapping("admin/vehicle")
class AdminVehicleController(private val vehicleBaseRepository: VehicleBaseRepository) {

    @GetMapping
    fun getVehicles(): List<VehicleBase> = vehicleBaseRepository.findAll()

    @PostMapping
    @Transactional
    fun saveVehicle(@RequestBody @Valid vehicle: VehicleBase): VehicleBase {
        if (vehicle.specialPart1 != null) {
            if (vehicle.specialPart1.slot != VehicleSlot.SPECIAL || vehicle.specialPart1Quality == null) {
                throw RuntimeException("Invalid special part 1")
            }
        } else { vehicle.specialPart1Quality = null }
        if (vehicle.specialPart2 != null) {
            if (vehicle.specialPart2.slot != VehicleSlot.SPECIAL || vehicle.specialPart2Quality == null) {
                throw RuntimeException("Invalid special part 2")
            }
        } else { vehicle.specialPart2Quality = null }
        if (vehicle.specialPart3 != null) {
            if (vehicle.specialPart3.slot != VehicleSlot.SPECIAL || vehicle.specialPart3Quality == null) {
                throw RuntimeException("Invalid special part 3")
            }
        } else { vehicle.specialPart3Quality = null }
        return vehicleBaseRepository.save(vehicle)
    }
}
