package io.pacworx.ambrosia.gear

import io.pacworx.ambrosia.hero.Rarity
import org.springframework.data.jpa.repository.JpaRepository

interface GearRepository: JpaRepository<Gear, Long> {
    fun findAllByPlayerIdAndEquippedToIsNull(playerId: Long): List<Gear>

    fun findAllByTypeAndRarity(type: GearType, rarity: Rarity): List<Gear>
}
