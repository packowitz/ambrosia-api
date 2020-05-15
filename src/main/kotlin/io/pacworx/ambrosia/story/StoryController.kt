package io.pacworx.ambrosia.story

import io.pacworx.ambrosia.hero.Color
import io.pacworx.ambrosia.player.Player
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@CrossOrigin(maxAge = 3600)
@RequestMapping("story")
class StoryController(
    private val storyRepository: StoryRepository,
    private val storyPlaceholderRepository: StoryPlaceholderRepository
) {

    @GetMapping("{trigger}")
    fun getStories(@ModelAttribute("player") player: Player, @PathVariable trigger: StoryTrigger): List<StoryResolved> {
        val placeholders = storyPlaceholderRepository.findAll()
        return storyRepository.findAllByTriggerOrderByNumber(trigger).map {
            StoryResolved(
                it.trigger,
                it.number,
                replace(it.title, placeholders, player.color),
                replace(it.message, placeholders, player.color)!!,
                replace(it.nextText, placeholders, player.color),
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
        val nextText: String?,
        val leftPic: String?,
        val rightPic: String?
    )
}
