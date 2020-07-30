package io.pacworx.ambrosia.achievements

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface PlayerAchievementRewardRepository : JpaRepository<PlayerAchievementReward, Long> {

    fun findAllByPlayerIdAndClaimedIsFalse(playerId: Long): List<PlayerAchievementReward>

    fun findByPlayerIdAndRewardId(playerId: Long, rewardId: Long): PlayerAchievementReward?
}