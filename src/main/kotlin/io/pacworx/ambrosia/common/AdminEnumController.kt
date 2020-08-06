package io.pacworx.ambrosia.common

import io.pacworx.ambrosia.loot.LootBoxType
import io.pacworx.ambrosia.maps.MapType
import io.pacworx.ambrosia.story.StoryTrigger
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@CrossOrigin(maxAge = 3600)
@RequestMapping("enums/admin")
class AdminEnumController {

    @GetMapping
    fun getAllEnums(): AdminEnums = AdminEnums()

    data class AdminEnums(
        val storyTriggers: List<StoryTrigger> = StoryTrigger.values().asList(),
        val mapTypes: List<MapType> = MapType.values().asList(),
        val lootBoxTypes: List<LootBoxType> = LootBoxType.values().asList()
    )
}
