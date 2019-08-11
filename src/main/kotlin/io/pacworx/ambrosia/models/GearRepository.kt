package io.pacworx.ambrosia.io.pacworx.ambrosia.models

import org.springframework.data.jpa.repository.JpaRepository

interface GearRepository: JpaRepository<Gear, Long> {
    fun findAllByPlayerIdAndEquippedToIsNull(playerId: Long): List<Gear>
}