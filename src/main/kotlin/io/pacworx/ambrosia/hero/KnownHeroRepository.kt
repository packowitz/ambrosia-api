package io.pacworx.ambrosia.hero

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface KnownHeroRepository : JpaRepository<KnownHero, Long> {

    fun findAllByPlayerId(playerId: Long): List<KnownHero>

    fun findByPlayerIdAndHeroBaseId(playerId: Long, heroBaseId: Long): KnownHero?
}