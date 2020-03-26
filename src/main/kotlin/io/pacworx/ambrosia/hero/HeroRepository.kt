package io.pacworx.ambrosia.hero

import org.springframework.data.jpa.repository.JpaRepository

interface HeroRepository: JpaRepository<Hero, Long> {

    fun findAllByPlayerIdOrderByLevelDescStarsDescHeroBase_IdAscIdAsc(playerId: Long): List<Hero>

    fun findAllByPlayerIdAndIdIn(playerId: Long, ids: List<Long>): List<Hero>

    fun deleteAllByIdIn(ids: List<Long>)
}