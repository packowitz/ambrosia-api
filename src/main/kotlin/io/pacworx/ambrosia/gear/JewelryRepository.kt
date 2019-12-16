package io.pacworx.ambrosia.io.pacworx.ambrosia.models

import io.pacworx.ambrosia.io.pacworx.ambrosia.enums.JewelType
import org.springframework.data.jpa.repository.JpaRepository

interface JewelryRepository: JpaRepository<Jewelry, Long> {

    fun findAllByPlayerId(playerId: Long): List<Jewelry>

    fun findByPlayerIdAndType(playerId: Long, type: JewelType): Jewelry?
}
