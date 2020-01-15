package io.pacworx.ambrosia.io.pacworx.ambrosia.player

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface PlayerRepository: JpaRepository<Player, Long> {
    fun findByEmailIgnoreCase(email: String): Player?

    fun findByServiceAccountIsTrueOrderByName(): List<Player>

    fun findByServiceAccountIsTrueAndId(id: Long): Player

    @Query("select count(*) from player", nativeQuery = true)
    fun numberOfPlayers(): Int
}
