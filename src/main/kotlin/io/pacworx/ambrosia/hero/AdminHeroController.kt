package io.pacworx.ambrosia.hero

import io.pacworx.ambrosia.common.PlayerActionResponse
import io.pacworx.ambrosia.player.Player
import org.springframework.web.bind.annotation.*

@RestController
@CrossOrigin(maxAge = 3600)
@RequestMapping("admin/hero")
class AdminHeroController(val heroBaseRepository: HeroBaseRepository,
                          val heroService: HeroService) {

    @PostMapping("recruit/{heroId}")
    fun recruit(@ModelAttribute("player") player: Player, @PathVariable heroId: Long): PlayerActionResponse {
        val hero = heroService.recruitHero(player, heroBaseRepository.getOne(heroId))
        return PlayerActionResponse(hero = heroService.asHeroDto(hero))
    }

}
