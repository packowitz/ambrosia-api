package io.pacworx.ambrosia.io.pacworx.ambrosia.dungeons

import org.springframework.web.bind.annotation.*

@RestController
@CrossOrigin(maxAge = 3600)
@RequestMapping("dungeon")
class DungeonController(private val dungeonRepository: DungeonRepository,
                        private val dungeonService: DungeonService) {

    @GetMapping
    fun getAllDungeons(): List<Dungeon> = dungeonRepository.findAll()

    @GetMapping("{id}")
    fun getDungeon(@PathVariable id: Long): DungeonService.DungeonResolved {
        val dungeon = dungeonRepository.getOne(id)
        return dungeonService.asDungeonResolved(dungeon)
    }
}
