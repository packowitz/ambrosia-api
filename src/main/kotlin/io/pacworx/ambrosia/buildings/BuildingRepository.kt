package io.pacworx.ambrosia.buildings

import io.pacworx.ambrosia.buildings.Building
import io.pacworx.ambrosia.buildings.BuildingType
import org.springframework.data.jpa.repository.JpaRepository

interface BuildingRepository: JpaRepository<Building, Long> {

    fun findAllByPlayerId(playerId: Long): List<Building>

    fun findByPlayerIdAndType(playerId: Long, type: BuildingType): Building?
}
