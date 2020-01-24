package io.pacworx.ambrosia.gear

import io.pacworx.ambrosia.enums.JewelType
import io.pacworx.ambrosia.gear.Jewelry
import org.springframework.data.jpa.repository.JpaRepository

interface JewelryRepository: JpaRepository<Jewelry, Long> {

    fun findAllByPlayerId(playerId: Long): List<Jewelry>

    fun findByPlayerIdAndType(playerId: Long, type: JewelType): Jewelry?
}
