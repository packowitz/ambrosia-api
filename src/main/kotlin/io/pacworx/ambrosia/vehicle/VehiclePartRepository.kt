package io.pacworx.ambrosia.vehicle

import org.springframework.data.jpa.repository.JpaRepository

interface VehiclePartRepository: JpaRepository<VehiclePart, Long>