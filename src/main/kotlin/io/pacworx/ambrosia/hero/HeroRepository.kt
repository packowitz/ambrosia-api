package io.pacworx.ambrosia.hero

import org.springframework.data.jpa.repository.JpaRepository

interface HeroRepository: JpaRepository<Hero, Long> {

    fun findAllByPlayerIdOrderByStarsDescLevelDescHeroBase_IdAscIdAsc(playerId: Long): List<Hero>
}