package io.pacworx.ambrosia.fights

import io.pacworx.ambrosia.exceptions.EntityNotFoundException
import io.pacworx.ambrosia.player.Player
import org.springframework.data.repository.findByIdOrNull
import org.springframework.web.bind.annotation.*

@RestController
@CrossOrigin(maxAge = 3600)
@RequestMapping("fight")
class FightController(private val fightRepository: FightRepository,
                      private val fightService: FightService
) {

    @GetMapping
    fun getAllFights(): List<Fight> = fightRepository.findAllByOrderByName()

    @GetMapping("{id}")
    fun getFight(@ModelAttribute("player") player: Player,
                 @PathVariable id: Long): FightService.FightResolved {
        val fight = fightRepository.findByIdOrNull(id) ?: throw EntityNotFoundException(player, "fight", id)
        return fightService.asFightResolved(fight)
    }
}
