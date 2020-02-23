package io.pacworx.ambrosia.vehicle

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface VehicleRepository: JpaRepository<Vehicle, Long> {

    fun findAllByPlayerId(playerId: Long): List<Vehicle>
}
