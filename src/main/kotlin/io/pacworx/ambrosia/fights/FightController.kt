package io.pacworx.ambrosia.fights

import org.springframework.web.bind.annotation.*

@RestController
@CrossOrigin(maxAge = 3600)
@RequestMapping("dungeon")
class FightController(private val fightRepository: FightRepository,
                      private val fightService: FightService
) {

    @GetMapping
    fun getAllFights(): List<Fight> = fightRepository.findAll()

    @GetMapping("{id}")
    fun getFight(@PathVariable id: Long): FightService.FightResolved {
        val fight = fightRepository.getOne(id)
        return fightService.asFightResolved(fight)
    }
}
