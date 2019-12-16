package io.pacworx.ambrosia.io.pacworx.ambrosia.models

import org.springframework.data.jpa.repository.JpaRepository

interface PlayerRepository: JpaRepository<Player, Long> {
    fun findByEmailIgnoreCase(email: String): Player?

    fun findByServiceAccountIsTrueOrderByName(): List<Player>

    fun findByServiceAccountIsTrueAndId(id: Long): Player
}
