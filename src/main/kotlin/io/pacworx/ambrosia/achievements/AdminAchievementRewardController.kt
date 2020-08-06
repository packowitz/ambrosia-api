package io.pacworx.ambrosia.achievements

import io.pacworx.ambrosia.exceptions.EntityNotFoundException
import io.pacworx.ambrosia.exceptions.GeneralException
import io.pacworx.ambrosia.loot.LootBoxRepository
import io.pacworx.ambrosia.loot.LootBoxType
import io.pacworx.ambrosia.loot.LootItemType
import io.pacworx.ambrosia.player.AuditLogService
import io.pacworx.ambrosia.player.Player
import org.springframework.data.repository.findByIdOrNull
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.*

@RestController
@CrossOrigin(maxAge = 3600)
@RequestMapping("admin/achievement_reward")
class AdminAchievementRewardController (
    private val achievementRewardRepository: AchievementRewardRepository,
    private val lootBoxRepository: LootBoxRepository,
    private val auditLogService: AuditLogService
) {

    @GetMapping
    fun getAllAchievementRewards(): List<AchievementReward> {
        val rewards = achievementRewardRepository.findAll()
        val sortedRewards = mutableListOf<AchievementReward>()
        rewards.filter { it.starter }.forEach { reward ->
            sortedRewards.add(reward)
            var current: AchievementReward? = reward
            while (current?.followUpReward != null) {
                current = rewards.find { it.id == current?.followUpReward }?.also { sortedRewards.add(it) }
            }
        }
        sortedRewards.addAll(rewards.filter { !sortedRewards.contains(it) })
        return sortedRewards
    }

    @PostMapping
    @Transactional
    fun saveAchievementReward(@ModelAttribute("player") player: Player,
                              @RequestBody achievementReward: AchievementReward): AchievementReward {
        val lootBox = lootBoxRepository.findByIdOrNull(achievementReward.lootBoxId)
            ?: throw EntityNotFoundException(player, "lootBox", achievementReward.lootBoxId)
        if (lootBox.type != LootBoxType.ACHIEVEMENT) {
            throw GeneralException(player, "Invalid achievement reward", "Loot must be of type ACHIEVEMENT")
        }
        if (achievementReward.followUpReward != null && achievementReward.followUpReward!! <= 0) {
            achievementReward.followUpReward = null
        }
        achievementReward.followUpReward?.let {
            achievementRewardRepository.findByIdOrNull(it)
                ?: throw GeneralException(player, "Invalid achievement reward", "Follow up reward does not exist")
        }
        return achievementRewardRepository.save(achievementReward).also {
            auditLogService.log(player, "Create or Update achievement reward #${it.id}", adminAction = true)
        }
    }
}