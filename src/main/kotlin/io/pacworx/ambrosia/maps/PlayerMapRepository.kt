package io.pacworx.ambrosia.io.pacworx.ambrosia.maps

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface PlayerMapRepository: JpaRepository<PlayerMap, Long> {

    fun getAllByPlayerId(playerId: Long): List<PlayerMap>

    fun getByPlayerIdAndMapId(playerId: Long, mapId: Long): PlayerMap?
}