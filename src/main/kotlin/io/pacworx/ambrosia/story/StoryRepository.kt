package io.pacworx.ambrosia.story

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface StoryRepository : JpaRepository<Story, Long> {

    fun findAllByTriggerOrderByNumber(trigger: StoryTrigger): List<Story>

    @Query("select loot_box_id from story where trigger = :trigger and number = 1", nativeQuery = true)
    fun findLootBoxId(trigger: StoryTrigger): Long?
}
