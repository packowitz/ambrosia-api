package io.pacworx.ambrosia.achievements

import io.pacworx.ambrosia.exceptions.EntityNotFoundException
import io.pacworx.ambrosia.loot.LootBoxRepository
import io.pacworx.ambrosia.loot.LootedItem
import io.pacworx.ambrosia.player.Player
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class AchievementService(
    private val playerAchievementRewardRepository: PlayerAchievementRewardRepository,
    private val achievementRewardRepository: AchievementRewardRepository,
    private val lootBoxRepository: LootBoxRepository
) {

    fun getActiveAchievementRewards(player: Player): List<AchievementReward> {
        val activePlayerRewards = playerAchievementRewardRepository.findAllByPlayerIdAndClaimedIsFalse(player.id)
        val activeRewards = achievementRewardRepository.findAllById(activePlayerRewards.map { it.rewardId })
        val unknownRewards = achievementRewardRepository.findUnknownStarterAchievementRewards(player.id)
        unknownRewards.forEach {
            playerAchievementRewardRepository.save(PlayerAchievementReward(playerId = player.id, rewardId = it.id))
        }
        return (activeRewards + unknownRewards).also { rewards -> rewards.forEach { addLootItems(player, it) } }
    }

    fun getAchievementReward(player: Player, achievementRewardId: Long): AchievementReward {
        return achievementRewardRepository.getOne(achievementRewardId).also { addLootItems(player, it) }
    }

    private fun addLootItems(player: Player, achievementReward: AchievementReward) {
        val lootBox = lootBoxRepository.findByIdOrNull(achievementReward.lootBoxId)
            ?: throw EntityNotFoundException(player, "lootBox", achievementReward.lootBoxId)
        achievementReward.reward = lootBox.items.map {
            LootedItem(
                type = it.type,
                resourceType = it.resourceType,
                progressStat = it.progressStat,
                jewelType = it.getJewelTypes().takeIf { list -> list.size == 1 }?.first(),
                value = when {
                    it.resourceType != null -> it.resourceFrom
                    it.progressStatBonus != null -> it.progressStatBonus
                    it.jewelLevel != null -> it.jewelLevel
                    else -> 0
                }?.toLong() ?: 0
            )
        }
    }
}