package io.pacworx.ambrosia.io.pacworx.ambrosia.models

import org.springframework.data.jpa.repository.JpaRepository

interface HeroRepository: JpaRepository<Hero, Long> {

    fun findAllByPlayerId(playerId: Long): List<Hero>
}