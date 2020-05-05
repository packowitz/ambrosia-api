package io.pacworx.ambrosia.hero

import io.pacworx.ambrosia.hero.base.HeroBase
import org.springframework.data.jpa.repository.JpaRepository

interface HeroRepository: JpaRepository<Hero, Long> {

    fun findAllByPlayerIdOrderByLevelDescStarsDescHeroBase_IdAscIdAsc(playerId: Long): List<Hero>

    fun findAllByPlayerIdAndIdIn(playerId: Long, ids: List<Long>): List<Hero>

    fun findAllByHeroBase(heroBase: HeroBase): List<Hero>

    fun deleteAllByIdIn(ids: List<Long>)
}