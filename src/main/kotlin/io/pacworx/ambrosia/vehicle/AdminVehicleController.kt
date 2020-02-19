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
    fun saveVehicle(@RequestBody @Valid vehicle: VehicleBase): VehicleBase = vehicleBaseRepository.save(vehicle)
}
