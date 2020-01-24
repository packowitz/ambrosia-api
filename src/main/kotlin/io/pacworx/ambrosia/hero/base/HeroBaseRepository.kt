package io.pacworx.ambrosia.hero.base

import io.pacworx.ambrosia.enums.Color
import io.pacworx.ambrosia.enums.Rarity
import io.pacworx.ambrosia.hero.base.HeroBase
import org.springframework.data.jpa.repository.JpaRepository

interface HeroBaseRepository: JpaRepository<HeroBase, Long> {
    fun findByHeroClassAndRarityAndColor(heroClass: String, rarity: Rarity, color: Color): HeroBase?

    fun findAllByRarityAndRecruitableIsTrue(rarity: Rarity): List<HeroBase>
}
