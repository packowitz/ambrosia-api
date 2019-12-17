package io.pacworx.ambrosia.io.pacworx.ambrosia.dungeons

import io.pacworx.ambrosia.io.pacworx.ambrosia.models.Player
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.*

@RestController
@CrossOrigin(maxAge = 3600)
@RequestMapping("admin/dungeon")
class DungeonController(private val dungeonRepository: DungeonRepository) {

    @GetMapping
    fun getAllDungeons(): List<Dungeon> = dungeonRepository.findAll()

    @PostMapping("new")
    @Transactional
    fun createDungeopn(@PathVariable name: String, @RequestBody dungeon: Dungeon): Dungeon {
        return dungeonRepository.save(dungeon)
    }
}
