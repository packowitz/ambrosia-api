package io.pacworx.ambrosia.achievements

import io.pacworx.ambrosia.common.PlayerActionResponse
import io.pacworx.ambrosia.exceptions.EntityNotFoundException
import io.pacworx.ambrosia.exceptions.GeneralException
import io.pacworx.ambrosia.inbox.InboxMessageRepository
import io.pacworx.ambrosia.loot.LootService
import io.pacworx.ambrosia.player.Player
import io.pacworx.ambrosia.progress.ProgressRepository
import io.pacworx.ambrosia.resources.ResourcesService
import org.springframework.data.repository.findByIdOrNull
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime
import javax.transaction.Transactional

@RestController
@CrossOrigin(maxAge = 3600)
@RequestMapping("achievement")
class AchievementController(
    private val progressRepository: ProgressRepository,
    private val resourcesService: ResourcesService,
    private val achievementsRepository: AchievementsRepository,
    private val playerAchievementRewardRepository: PlayerAchievementRewardRepository,
    private val achievementRewardRepository: AchievementRewardRepository,
    private val achievementService: AchievementService,
    private val lootService: LootService,
    private val inboxMessageRepository: InboxMessageRepository
) {

    @PostMapping("claim/{achievementId}")
    @Transactional
    fun claim(@ModelAttribute("player") player: Player,
              @PathVariable achievementId: Long): PlayerActionResponse {
        val timestamp = LocalDateTime.now()
        val playerAchievementReward = playerAchievementRewardRepository.findByPlayerIdAndRewardId(player.id, achievementId)
            ?: throw EntityNotFoundException(player, "playerAchievementReward", achievementId)
        if (playerAchievementReward.claimed) {
            throw GeneralException(player, "Cannot claim achievement", "Achievement already claimed")
        }
        val achievementReward = achievementRewardRepository.findByIdOrNull(achievementId)
            ?: throw EntityNotFoundException(player, "achievementReward", achievementId)
        val achievements = achievementsRepository.getOne(player.id)
        if (achievementReward.achievementType.getAmount(achievements) < achievementReward.achievementAmount) {
            throw GeneralException(player, "Cannot claim achievement", "Achievement not fulfilled")
        }

        val result = lootService.openLootBox(player, achievementReward.lootBoxId, achievements)
        playerAchievementReward.claimed = true
        val newAchievement = achievementReward.followUpReward
            ?.let { achievementService.getAchievementReward(player, it) }
            ?.also { playerAchievementRewardRepository.save(PlayerAchievementReward(playerId = player.id, rewardId = it.id)) }

        return PlayerActionResponse(
            resources = resourcesService.getResources(player),
            achievements = achievements,
            progress = if (result.items.any { it.progress != null }) { progressRepository.getOne(player.id) } else { null },
            heroes = result.items.filter { it.hero != null }.map { it.hero!! }.takeIf { it.isNotEmpty() },
            gears = result.items.filter { it.gear != null }.map { it.gear!! }.takeIf{ it.isNotEmpty() },
            jewelries = result.items.filter { it.jewelry != null }.map { it.jewelry!! }.takeIf { it.isNotEmpty() },
            vehicles = result.items.filter { it.vehicle != null }.map { it.vehicle!! }.takeIf { it.isNotEmpty() },
            vehicleParts = result.items.filter { it.vehiclePart != null }.map { it.vehiclePart!! }.takeIf { it.isNotEmpty() },
            achievementRewards = listOfNotNull(newAchievement).takeIf { it.isNotEmpty() },
            claimedAchievementRewardId = achievementId,
            inboxMessages = inboxMessageRepository.findAllByPlayerIdAndSendTimestampIsAfter(player.id, timestamp.minusSeconds(1))
        )
    }
}