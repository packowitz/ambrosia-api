package io.pacworx.ambrosia.vehicle

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface VehiclePartRepository: JpaRepository<VehiclePart, Long> {

    fun findAllByPlayerIdAndEquippedToIsNull(playerId: Long): List<VehiclePart>
}
