package io.pacworx.ambrosia.io.pacworx.ambrosia.controller

import io.pacworx.ambrosia.io.pacworx.ambrosia.enums.Rarity
import io.pacworx.ambrosia.io.pacworx.ambrosia.enums.RecruitType
import io.pacworx.ambrosia.io.pacworx.ambrosia.models.*
import io.pacworx.ambrosia.io.pacworx.ambrosia.player.Player
import io.pacworx.ambrosia.io.pacworx.ambrosia.services.HeroService
import org.springframework.web.bind.annotation.*

@RestController
@CrossOrigin(maxAge = 3600)
@RequestMapping("hero")
class HeroController(val heroService: HeroService,
                          val heroRepository: HeroRepository,
                          val heroBaseRepository: HeroBaseRepository) {

    @GetMapping("")
    fun getHeroes(@ModelAttribute("player") player: Player): List<HeroDto> {
        return heroRepository.findAllByPlayerIdOrderByStarsDescLevelDescHeroBase_IdAscIdAsc(player.id).map { heroService.asHeroDto(it) }
    }

    @PostMapping("recruit/{type}")
    fun recruitHero(@PathVariable type: RecruitType): PlayerActionResponse {
        val hero = when(type) {
            RecruitType.BASIC -> heroService.recruitHero(uncommonChance = 0.02, commonChance = 0.23, default = Rarity.SIMPLE)
            RecruitType.ADVANCED -> heroService.recruitHero(epicChance = 0.005, rareChance = 0.08, default = Rarity.UNCOMMON)
        }
        return PlayerActionResponse(hero = heroService.asHeroDto(hero))
    }

}
