package io.pacworx.ambrosia.buildings

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface IncubatorRepository : JpaRepository<Incubator, Long> {

    fun findAllByPlayerIdOrderByStartTimestamp(playerId: Long): List<Incubator>
}