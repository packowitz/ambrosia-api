package io.pacworx.ambrosia.io.pacworx.ambrosia.admin.controller

import io.pacworx.ambrosia.io.pacworx.ambrosia.controller.PlayerActionResponse
import io.pacworx.ambrosia.io.pacworx.ambrosia.models.Hero
import io.pacworx.ambrosia.io.pacworx.ambrosia.models.HeroBaseRepository
import io.pacworx.ambrosia.io.pacworx.ambrosia.models.HeroRepository
import io.pacworx.ambrosia.io.pacworx.ambrosia.models.Player
import io.pacworx.ambrosia.io.pacworx.ambrosia.services.HeroService
import org.springframework.web.bind.annotation.*

@RestController
@CrossOrigin(maxAge = 3600)
@RequestMapping("admin/hero")
class AdminHeroController(val heroRepository: HeroRepository,
                          val heroBaseRepository: HeroBaseRepository,
                          val heroService: HeroService) {

    @PostMapping("recruit/{heroId}")
    fun recruit(@ModelAttribute("player") player: Player, @PathVariable heroId: Long): PlayerActionResponse {
        val hero = heroBaseRepository.getOne(heroId).let { Hero(player.id, it) }
        heroRepository.save(hero)
        return PlayerActionResponse(hero = heroService.asHeroDto(hero))
    }

}
