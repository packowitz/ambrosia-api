package io.pacworx.ambrosia.oddjobs

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface OddJobRepository : JpaRepository<OddJob, Long> {
    fun findAllByPlayerIdOrderByCreated(playerId: Long): List<OddJob>
}