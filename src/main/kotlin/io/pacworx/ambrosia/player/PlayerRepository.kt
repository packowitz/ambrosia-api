package io.pacworx.ambrosia.player

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Lock
import org.springframework.data.jpa.repository.Query
import javax.persistence.LockModeType

interface PlayerRepository: JpaRepository<Player, Long> {
    fun findByEmailIgnoreCase(email: String): Player?

    @Lock(LockModeType.PESSIMISTIC_READ)
    fun findByIdAndLockedIsNull(playerId: Long): Player?

    @Lock(LockModeType.PESSIMISTIC_READ)
    fun findByIdAndLockedIsNotNull(playerId: Long): Player?

    fun findByServiceAccountIsTrueOrderByName(): List<Player>

    fun findByServiceAccountIsTrueAndId(id: Long): Player

    @Query("select count(*) from player", nativeQuery = true)
    fun numberOfPlayers(): Int
}
