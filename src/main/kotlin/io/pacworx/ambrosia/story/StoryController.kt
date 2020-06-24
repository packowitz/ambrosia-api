package io.pacworx.ambrosia.story

import io.pacworx.ambrosia.common.PlayerActionResponse
import io.pacworx.ambrosia.exceptions.GeneralException
import io.pacworx.ambrosia.hero.Color
import io.pacworx.ambrosia.loot.LootService
import io.pacworx.ambrosia.maps.PlayerMapResolved
import io.pacworx.ambrosia.player.AuditLogService
import io.pacworx.ambrosia.player.Player
import io.pacworx.ambrosia.progress.ProgressRepository
import io.pacworx.ambrosia.resources.ResourcesService
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.transaction.Transactional

@RestController
@CrossOrigin(maxAge = 3600)
@RequestMapping("story")
class StoryController(
    private val storyRepository: StoryRepository,
    private val storyPlaceholderRepository: StoryPlaceholderRepository,
    private val storyProgressRepository: StoryProgressRepository,
    private val lootService: LootService,
    private val resourcesService: ResourcesService,
    private val auditLogService: AuditLogService,
    private val progressRepository: ProgressRepository
) {

    @PostMapping("{trigger}/finish")
    @Transactional
    fun finishStory(@ModelAttribute("player") player: Player,
                    @PathVariable trigger: StoryTrigger): PlayerActionResponse {
        val storyProgresses = storyProgressRepository.findAllByPlayerId(player.id)
        if (storyProgresses.any { it.trigger == trigger }) {
            throw GeneralException(player, "Cannot finish story", "You've finished story ${trigger.name} already")
        }
        val lootBoxResult = storyRepository.findStoryByTriggerAndNumber(trigger)
            ?.takeIf { it.lootBoxId != null }
            ?.let {
                lootService.openLootBox(player, it.lootBoxId!!)
            }
        storyProgressRepository.save(StoryProgress(playerId = player.id, trigger = trigger))
        auditLogService.log(player, "Finished story ${trigger.name}" +
                "${lootBoxResult?.let { result -> " looting ${result.items.joinToString { it.auditLog() }}" } ?: ""}"
        )
        return PlayerActionResponse(
            knownStories = listOf(trigger.name),
            resources = resourcesService.getResources(player),
            progress = if (lootBoxResult?.items?.any { it.progress != null } == true) { progressRepository.getOne(player.id) } else { null },
            heroes = lootBoxResult?.items?.filter { it.hero != null }?.map { it.hero!! }?.takeIf { it.isNotEmpty() },
            gears = lootBoxResult?.items?.filter { it.gear != null }?.map { it.gear!! }?.takeIf{ it.isNotEmpty() },
            jewelries = lootBoxResult?.items?.filter { it.jewelry != null }?.map { it.jewelry!! }?.takeIf { it.isNotEmpty() },
            vehicles = lootBoxResult?.items?.filter { it.vehicle != null }?.map { it.vehicle!! }?.takeIf { it.isNotEmpty() },
            vehicleParts = lootBoxResult?.items?.filter { it.vehiclePart != null }?.map { it.vehiclePart!! }?.takeIf { it.isNotEmpty() },
            looted = lootBoxResult?.items?.map { lootService.asLooted(it) }
        )
    }

    @GetMapping("{trigger}")
    fun getStories(@ModelAttribute("player") player: Player, @PathVariable trigger: StoryTrigger): List<StoryResolved> {
        val placeholders = storyPlaceholderRepository.findAll()
        return storyRepository.findAllByTriggerOrderByNumber(trigger).map {
            StoryResolved(
                it.trigger,
                it.number,
                replace(it.title, placeholders, player.color),
                replace(it.message, placeholders, player.color)!!,
                replace(it.buttonText, placeholders, player.color)!!,
                replace(it.leftPic, placeholders, player.color),
                replace(it.rightPic, placeholders, player.color)
            )
        }
    }

    private fun replace(text: String?, placeholders: List<StoryPlaceholder>, color: Color?): String? {
        return text?.let {
            var replaced = it
            placeholders.forEach { placeholder ->
                replaced = when(color) {
                    Color.RED -> replaced.replace(placeholder.identifier, placeholder.red)
                    Color.GREEN -> replaced.replace(placeholder.identifier, placeholder.green)
                    Color.BLUE -> replaced.replace(placeholder.identifier, placeholder.blue)
                    else -> replaced
                }
            }
            replaced
        }
    }

    data class StoryResolved(
        val trigger: StoryTrigger,
        val number: Int,
        val title: String?,
        val message: String,
        val buttonText: String,
        val leftPic: String?,
        val rightPic: String?
    )
}
