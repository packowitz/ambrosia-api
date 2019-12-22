package io.pacworx.ambrosia.io.pacworx.ambrosia.dungeons

import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@RestController
@CrossOrigin(maxAge = 3600)
@RequestMapping("admin/dungeon")
class AdminDungeonController(private val dungeonRepository: DungeonRepository,
                             private val dungeonService: DungeonService) {

    @PostMapping("new")
    @Transactional
    fun createDungeopn(@RequestBody dungeon: Dungeon): Dungeon {
        return dungeonRepository.save(dungeon)
    }

    @PutMapping("{id}")
    @Transactional
    fun updateDungeon(@PathVariable id: Long, @RequestBody @Valid dungeon: Dungeon): DungeonService.DungeonResolved {
        return dungeonService.asDungeonResolved(dungeonRepository.save(dungeon))
    }
}