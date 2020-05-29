package io.pacworx.ambrosia.battle

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.time.Instant
import java.time.temporal.ChronoUnit

interface BattleRepository : JpaRepository<Battle, Long> {

    fun findTopByPlayerIdAndStatusInAndPreviousBattleIdNull(playerId: Long, status: List<BattleStatus>): Battle?

    fun findTopByPlayerIdAndStatusNotIn(playerId: Long, status: List<BattleStatus>): Battle?

    fun findTop10ByLastActionBeforeAndStatusInAndPreviousBattleIdNull(
        lastAction: Instant = Instant.now().minus(1, ChronoUnit.DAYS),
        states: List<BattleStatus> = listOf(BattleStatus.WON, BattleStatus.LOST)): List<Battle>

    @Query("select id from battle where previous_battle_id is null and (hero1id = :heroId or hero2id = :heroId or hero3id = :heroId or hero4id = :heroId or opp_hero1id = :heroId or opp_hero2id = :heroId or opp_hero3id = :heroId or opp_hero4id = :heroId)", nativeQuery = true)
    fun findAllByContainingHero(@Param("heroId") heroId: Long): List<Long>
}
