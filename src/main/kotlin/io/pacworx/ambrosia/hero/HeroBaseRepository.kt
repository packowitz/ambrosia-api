package io.pacworx.ambrosia.hero

import io.pacworx.ambrosia.hero.Color
import io.pacworx.ambrosia.hero.HeroBase
import io.pacworx.ambrosia.hero.Rarity
import org.springframework.data.jpa.repository.JpaRepository

interface HeroBaseRepository: JpaRepository<HeroBase, Long> {
    fun findByHeroClassAndRarityAndColor(heroClass: String, rarity: Rarity, color: Color): HeroBase?

    fun findAllByRarityAndRecruitableIsTrue(rarity: Rarity): List<HeroBase>

    fun findAllByStartingHeroIsTrueAndColor(color: Color): List<HeroBase>
}
