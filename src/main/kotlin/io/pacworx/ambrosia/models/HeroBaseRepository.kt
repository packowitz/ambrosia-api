package io.pacworx.ambrosia.io.pacworx.ambrosia.models

import io.pacworx.ambrosia.io.pacworx.ambrosia.enums.Color
import io.pacworx.ambrosia.io.pacworx.ambrosia.enums.Rarity
import io.pacworx.ambrosia.models.HeroBase
import org.springframework.data.jpa.repository.JpaRepository

interface HeroBaseRepository: JpaRepository<HeroBase, Long> {
    fun findByHeroClassAndRarityAndColor(heroClass: String, rarity: Rarity, color: Color): HeroBase?

    fun findAllByRarityAndRecruitableIsTrue(rarity: Rarity): List<HeroBase>
}