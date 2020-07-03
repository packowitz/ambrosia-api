package io.pacworx.ambrosia.oddjobs

import io.pacworx.ambrosia.hero.Rarity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface OddJobBaseRepository : JpaRepository<OddJobBase, Long> {

    fun findAllByActiveIsTrueAndLevelAndRarity(level: Int, rarity: Rarity): List<OddJobBase>
}