package io.pacworx.ambrosia.story

import org.springframework.web.bind.annotation.*
import javax.transaction.Transactional

@RestController
@CrossOrigin(maxAge = 3600)
@RequestMapping("admin/story")
class AdminStoryController(
    private val storyRepository: StoryRepository,
    private val storyPlaceholderRepository: StoryPlaceholderRepository
) {

    @GetMapping("placeholder")
    fun getPlaceholder(): List<StoryPlaceholder> = storyPlaceholderRepository.findAll()

    @PostMapping("placeholder")
    @Transactional
    fun savePlaceholder(@RequestBody placeholder: StoryPlaceholder): StoryPlaceholder {
        return storyPlaceholderRepository.save(placeholder)
    }

    @GetMapping("{trigger}")
    fun getStories(@PathVariable trigger: StoryTrigger): List<Story> {
        return storyRepository.findAllByTriggerOrderByNumber(trigger)
    }

    @PostMapping
    @Transactional
    fun saveStory(@RequestBody story: Story): Story {
        return storyRepository.save(story)
    }

    @DeleteMapping("{id}")
    @Transactional
    fun deleteStory(@PathVariable("id") id: Long) {
        storyRepository.deleteById(id)
    }
}