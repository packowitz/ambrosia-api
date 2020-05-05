package io.pacworx.ambrosia.battle.offline

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface MissionRepository : JpaRepository<Mission, Long> {
    fun findAllByPlayerIdOrderBySlotNumber(playerId: Long): List<Mission>

    @Query("select id from mission where hero1id = :heroId or hero2id = :heroId or hero3id = :heroId or hero4id = :heroId", nativeQuery = true)
    fun findAllByContainingHero(@Param("heroId") heroId: Long): List<Long>
}
