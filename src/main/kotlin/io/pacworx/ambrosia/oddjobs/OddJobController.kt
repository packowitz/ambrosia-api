package io.pacworx.ambrosia.oddjobs

import io.pacworx.ambrosia.achievements.AchievementsRepository
import io.pacworx.ambrosia.common.PlayerActionResponse
import io.pacworx.ambrosia.exceptions.EntityNotFoundException
import io.pacworx.ambrosia.exceptions.GeneralException
import io.pacworx.ambrosia.exceptions.UnauthorizedException
import io.pacworx.ambrosia.inbox.InboxMessageRepository
import io.pacworx.ambrosia.loot.*
import io.pacworx.ambrosia.player.Player
import io.pacworx.ambrosia.progress.ProgressRepository
import io.pacworx.ambrosia.properties.PropertyService
import io.pacworx.ambrosia.properties.PropertyType
import io.pacworx.ambrosia.resources.ResourcesService
import org.springframework.data.repository.findByIdOrNull
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime
import javax.transaction.Transactional

@RestController
@CrossOrigin(maxAge = 3600)
@RequestMapping("oddjob")
class OddJobController(
    private val oddJobRepository: OddJobRepository,
    private val lootService: LootService,
    private val resourcesService: ResourcesService,
    private val progressRepository: ProgressRepository,
    private val dailyActivityRepository: DailyActivityRepository,
    private val propertyService: PropertyService,
    private val achievementsRepository: AchievementsRepository,
    private val inboxMessageRepository: InboxMessageRepository
) {

    @PostMapping("{oddJobId}/remove")
    @Transactional
    fun removeOddJob(@ModelAttribute("player") player: Player,
                     @PathVariable oddJobId: Long): PlayerActionResponse {
        val oddJob = oddJobRepository.findByIdOrNull(oddJobId)
            ?: throw EntityNotFoundException(player, "OddJob", oddJobId)
        if (oddJob.playerId != player.id) {
            throw UnauthorizedException(player, "You can only remove an odd job you own")
        }
        oddJobRepository.delete(oddJob)
        return PlayerActionResponse(
            oddJobDone = oddJobId
        )
    }

    @PostMapping("{oddJobId}/claim")
    @Transactional
    fun claimOddJob(@ModelAttribute("player") player: Player,
                    @PathVariable oddJobId: Long): PlayerActionResponse {
        val timestamp = LocalDateTime.now()
        val oddJob = oddJobRepository.findByIdOrNull(oddJobId)
            ?: throw EntityNotFoundException(player, "OddJob", oddJobId)
        if (oddJob.playerId != player.id) {
            throw UnauthorizedException(player, "You can only remove an odd job you own")
        }
        if (oddJob.jobAmountDone < oddJob.jobAmount) {
            throw GeneralException(player, "Cannot claim odd job", "Odd job is not completed yet.")
        }
        val achievements = achievementsRepository.getOne(player.id)
        achievements.oddJobsDone ++
        val result = lootService.openLootBox(player, oddJob.lootBoxId, achievements)
        val dailyActivity = dailyActivityRepository.getOne(player.id)
        oddJobRepository.delete(oddJob)
        return PlayerActionResponse(
            resources = resourcesService.getResources(player),
            progress = progressRepository.getOne(player.id),
            achievements = achievements,
            oddJobDone = oddJobId,
            dailyActivity = dailyActivity.takeIf { it.activityDetected() },
            inboxMessages = inboxMessageRepository.findAllByPlayerIdAndSendTimestampIsAfter(player.id, timestamp.minusSeconds(1)),
            looted = Looted(LootedType.ODD_JOB, result.items.map { lootService.asLootedItem(it) })
        )
    }

    @PostMapping("daily/{day}")
    @Transactional
    fun claimDailyActivity(@ModelAttribute("player") player: Player,
                           @PathVariable day: Int): PlayerActionResponse {
        val timestamp = LocalDateTime.now()
        val dailyActivity = dailyActivityRepository.getOne(player.id)
        if (!dailyActivity.isClaimable(day)) {
            throw GeneralException(player, "Cannot claim daily", "Day $day is not claimable for you")
        }
        val lootedItems = mutableListOf<LootedItem>()
        val resources = resourcesService.getResources(player)
        propertyService.getProperties(PropertyType.DAILY_REWARD, day).forEach {
            resourcesService.gainResources(resources, it.resourceType!!, it.value1)
            lootedItems.add(LootedItem(LootItemType.RESOURCE, it.resourceType, value = it.value1.toLong()))
        }
        dailyActivity.claim(day)
        val achievements = achievementsRepository.getOne(player.id)
        achievements.dailyRewardsClaimed ++
        return PlayerActionResponse(
            resources = resources,
            achievements = achievements,
            dailyActivity = dailyActivity,
            inboxMessages = inboxMessageRepository.findAllByPlayerIdAndSendTimestampIsAfter(player.id, timestamp.minusSeconds(1)),
            looted = Looted(LootedType.DAILY_ACTIVITY, lootedItems)
        )
    }
}