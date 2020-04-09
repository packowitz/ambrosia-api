package io.pacworx.ambrosia.upgrade

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UpgradeRepository : JpaRepository<Upgrade, Long> {

    fun findAllByPlayerIdOrderByPositionAsc(playerId: Long): List<Upgrade>
}