package io.pacworx.ambrosia.gear

import org.springframework.data.jpa.repository.JpaRepository

interface GearRepository: JpaRepository<Gear, Long> {
    fun findAllByPlayerIdAndEquippedToIsNull(playerId: Long): List<Gear>
}