package io.pacworx.ambrosia.hero

import io.pacworx.ambrosia.common.PlayerActionResponse
import io.pacworx.ambrosia.enums.Rarity
import io.pacworx.ambrosia.enums.RecruitType
import io.pacworx.ambrosia.hero.base.HeroBaseRepository
import io.pacworx.ambrosia.player.Player
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
