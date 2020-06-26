package io.pacworx.ambrosia.expedition

import io.pacworx.ambrosia.hero.Rarity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ExpeditionBaseRepository: JpaRepository<ExpeditionBase, Long> {

    fun findAllByLevelAndRarity(level: Int, rarity: Rarity): List<ExpeditionBase>
}