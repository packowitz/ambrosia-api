package io.pacworx.ambrosia.io.pacworx.ambrosia.models

import org.springframework.data.jpa.repository.JpaRepository

interface HeroRepository: JpaRepository<Hero, Long> {

    fun findAllByPlayerIdOrderByStarsDescLevelDescHeroBase_IdAscIdAsc(playerId: Long): List<Hero>
}