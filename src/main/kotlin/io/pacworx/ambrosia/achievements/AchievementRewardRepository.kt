package io.pacworx.ambrosia.achievements

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface AchievementRewardRepository : JpaRepository<AchievementReward, Long> {

    @Query("""
        select ar.* from achievement_reward ar where ar.starter is true and not exists(select 1 from player_achievement_reward par where par.player_id = :playerId and par.reward_id = ar.id) 
    """, nativeQuery = true)
    fun findUnknownStarterAchievementRewards(@Param("playerId") playerId: Long): List<AchievementReward>
}