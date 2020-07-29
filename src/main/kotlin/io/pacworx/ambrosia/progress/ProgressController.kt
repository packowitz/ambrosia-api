package io.pacworx.ambrosia.progress

import io.pacworx.ambrosia.achievements.AchievementsRepository
import io.pacworx.ambrosia.common.PlayerActionResponse
import io.pacworx.ambrosia.loot.LootItemType
import io.pacworx.ambrosia.loot.Looted
import io.pacworx.ambrosia.loot.LootedItem
import io.pacworx.ambrosia.loot.LootedType
import io.pacworx.ambrosia.player.Player
import io.pacworx.ambrosia.properties.PropertyService
import io.pacworx.ambrosia.properties.PropertyType
import io.pacworx.ambrosia.resources.ResourcesService
import org.springframework.web.bind.annotation.*
import javax.transaction.Transactional

@RestController
@CrossOrigin(maxAge = 3600)
@RequestMapping("progress")
class ProgressController(
    private val progressRepository: ProgressRepository,
    private val propertyService: PropertyService,
    private val resourcesService: ResourcesService,
    private val achievementsRepository: AchievementsRepository
) {

    @PostMapping("level_up")
    @Transactional
    fun levelUp(@ModelAttribute("player") player: Player): PlayerActionResponse {
        val lootedItems = mutableListOf<LootedItem>()
        val progress = progressRepository.getOne(player.id)
        val resources = resourcesService.getResources(player)
        if (progress.xp >= progress.maxXp && progress.level < 60) {
            progress.level ++
            progress.xp -= progress.maxXp
            progress.maxXp = propertyService.getPlayerMaxXp(progress.level)
            propertyService.getProperties(PropertyType.LEVEL_REWARD_PLAYER, progress.level).forEach {
                if (it.resourceType != null) {
                    resourcesService.gainResources(resources, it.resourceType, it.value1)
                    lootedItems.add(LootedItem(
                        type = LootItemType.RESOURCE,
                        resourceType = it.resourceType,
                        value = it.value1.toLong()
                    ))
                }
                if (it.progressStat != null) {
                    it.progressStat.apply(progress, it.value1)
                    lootedItems.add(LootedItem(
                        type = LootItemType.PROGRESS,
                        progressStat = it.progressStat,
                        value = it.value1.toLong()
                    ))
                }
            }
        }
        return PlayerActionResponse(
            progress = progress,
            resources = resources,
            looted = lootedItems.takeIf { it.isNotEmpty() }?.let { Looted(LootedType.LEVEL_UP, it) }
        )
    }

    @PostMapping("vip_level_up")
    @Transactional
    fun vipLevelUp(@ModelAttribute("player") player: Player): PlayerActionResponse {
        val lootedItems = mutableListOf<LootedItem>()
        val progress = progressRepository.getOne(player.id)
        val resources = resourcesService.getResources(player)
        if (progress.vipPoints >= progress.vipMaxPoints && progress.vipLevel < 20) {
            progress.vipLevel ++
            progress.vipMaxPoints = propertyService.getPlayerVipMax(progress.level)
            propertyService.getProperties(PropertyType.VIP_LEVEL_REWARD_PLAYER, progress.vipLevel).forEach {
                if (it.resourceType != null) {
                    resourcesService.gainResources(resources, it.resourceType, it.value1)
                    lootedItems.add(LootedItem(
                        type = LootItemType.RESOURCE,
                        resourceType = it.resourceType,
                        value = it.value1.toLong()
                    ))
                }
                if (it.progressStat != null) {
                    it.progressStat.apply(progress, it.value1)
                    lootedItems.add(LootedItem(
                        type = LootItemType.PROGRESS,
                        progressStat = it.progressStat,
                        value = it.value1.toLong()
                    ))
                }
            }
        }
        return PlayerActionResponse(
            progress = progress,
            resources = resources,
            looted = lootedItems.takeIf { it.isNotEmpty() }?.let { Looted(LootedType.VIP_LEVEL_UP, it) }
        )
    }

    @PostMapping("exp_level_up")
    @Transactional
    fun expLevelUp(@ModelAttribute("player") player: Player): PlayerActionResponse {
        val lootedItems = mutableListOf<LootedItem>()
        val progress = progressRepository.getOne(player.id)
        if (progress.expeditionLevel < 6) {
            val achievements =  achievementsRepository.getOne(player.id)
            val expToNextLevel = propertyService.getProperties(PropertyType.EXPEDITION_PROGRESS, progress.expeditionLevel).first().value1
            if (achievements.expeditionsDone >= expToNextLevel) {
                ProgressStat.EXPEDITION_LEVEL.apply(progress, 1)
                lootedItems.add(LootedItem(
                    type = LootItemType.PROGRESS,
                    progressStat = ProgressStat.EXPEDITION_LEVEL,
                    value = 1
                ))
            }
        }
        return PlayerActionResponse(
            progress = progress,
            looted = lootedItems.takeIf { it.isNotEmpty() }?.let { Looted(LootedType.EXP_LEVEL_UP, it) }
        )
    }
}