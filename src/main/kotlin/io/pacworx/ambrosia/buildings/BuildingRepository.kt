package io.pacworx.ambrosia.buildings

import io.pacworx.ambrosia.io.pacworx.ambrosia.buildings.Building
import org.springframework.data.jpa.repository.JpaRepository

interface BuildingRepository: JpaRepository<Building, Long> {

    fun findAllByPlayerId(playerId: Long): List<Building>
}