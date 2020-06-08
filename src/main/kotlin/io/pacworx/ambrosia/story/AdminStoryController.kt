package io.pacworx.ambrosia.story

import io.pacworx.ambrosia.player.AuditLogService
import io.pacworx.ambrosia.player.Player
import org.springframework.web.bind.annotation.*
import javax.transaction.Transactional

@RestController
@CrossOrigin(maxAge = 3600)
@RequestMapping("admin/story")
class AdminStoryController(
    private val storyRepository: StoryRepository,
    private val storyPlaceholderRepository: StoryPlaceholderRepository,
    private val auditLogService: AuditLogService
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
        return storyRepository.saveAll(request.stories)
    }

    data class SaveStoryLineRequest(
        val stories: List<Story>,
        val toDelete: List<Long>?
    )
}
