package io.pacworx.ambrosia.story

import io.pacworx.ambrosia.common.PlayerActionResponse
import io.pacworx.ambrosia.exceptions.EntityNotFoundException
import io.pacworx.ambrosia.exceptions.GeneralException
import io.pacworx.ambrosia.loot.LootBoxRepository
import io.pacworx.ambrosia.loot.LootBoxResult
import io.pacworx.ambrosia.loot.LootBoxType
import io.pacworx.ambrosia.player.AuditLogService
import io.pacworx.ambrosia.player.Player
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import javax.transaction.Transactional

@RestController
@CrossOrigin(maxAge = 3600)
@RequestMapping("admin/story")
class AdminStoryController(
    private val storyRepository: StoryRepository,
    private val storyPlaceholderRepository: StoryPlaceholderRepository,
    private val auditLogService: AuditLogService,
    private val storyProgressRepository: StoryProgressRepository,
    private val lootBoxRepository: LootBoxRepository
) {

    @GetMapping("placeholder")
    fun getPlaceholder(): List<StoryPlaceholder> = storyPlaceholderRepository.findAll()

    @PostMapping("placeholder")
    @Transactional
    fun savePlaceholder(@ModelAttribute("player") player: Player,
                        @RequestBody placeholder: StoryPlaceholder): StoryPlaceholder {
        auditLogService.log(player, "Save placeholder ${placeholder.identifier}", adminAction = true)
        return storyPlaceholderRepository.save(placeholder)
    }

    @GetMapping("{trigger}")
    fun getStories(@PathVariable trigger: StoryTrigger): List<Story> {
        return storyRepository.findAllByTriggerOrderByNumber(trigger)
    }

    @PostMapping
    @Transactional
    fun saveStoryLine(@ModelAttribute("player") player: Player,
                      @RequestBody request: SaveStoryLineRequest): List<Story> {
        auditLogService.log(player, "Save story line ${request.stories.getOrNull(0)?.trigger?.name ?: "unknown"}", adminAction = true)
        request.toDelete?.takeIf { it.isNotEmpty() }?.forEach { storyRepository.deleteById(it) }
        if (request.lootBoxId != null) {
            val lootBox = lootBoxRepository.findByIdOrNull(request.lootBoxId)
                ?: throw EntityNotFoundException(player, "lootBox", request.lootBoxId)
            if (lootBox.type != LootBoxType.STORY) {
                throw GeneralException(player, "Invalid loot box", "Loot must be of type STORY")
            }
        }
        request.stories.find { it.number == 1 }?.lootBoxId = request.lootBoxId
        request.stories.filter { it.number != 1 }.forEach { it.lootBoxId = null }
        return storyRepository.saveAll(request.stories)
    }

    @PostMapping("reset")
    @Transactional
    fun resetStoryLine(@ModelAttribute("player") player: Player): PlayerActionResponse {
        storyProgressRepository.findAllByPlayerId(player.id).forEach {
            storyProgressRepository.delete(it)
        }
        auditLogService.log(player, "Reset his story line", adminAction = true)
        return PlayerActionResponse()
    }

    data class SaveStoryLineRequest(
        val stories: List<Story>,
        val toDelete: List<Long>?,
        val lootBoxId: Long?
    )
}
