package io.pacworx.ambrosia.io.pacworx.ambrosia.models

import org.springframework.data.jpa.repository.JpaRepository

interface PlayerRepository: JpaRepository<Player, Long> {
    fun findByEmail(email: String): Player?
}
