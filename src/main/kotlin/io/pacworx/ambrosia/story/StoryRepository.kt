package io.pacworx.ambrosia.story

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface StoryRepository : JpaRepository<Story, Long> {

    fun findAllByTriggerOrderByNumber(trigger: StoryTrigger): List<Story>
}
