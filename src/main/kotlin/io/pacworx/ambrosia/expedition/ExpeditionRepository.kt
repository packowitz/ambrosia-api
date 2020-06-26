package io.pacworx.ambrosia.expedition

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ExpeditionRepository : JpaRepository<Expedition, Long> {

    fun findAllByExpeditionBase_LevelAndActiveIsTrue(level: Int): List<Expedition>
}