package io.pacworx.ambrosia.io.pacworx.ambrosia.controller

import io.pacworx.ambrosia.io.pacworx.ambrosia.enums.Rarity
import io.pacworx.ambrosia.io.pacworx.ambrosia.enums.RecruitType
import io.pacworx.ambrosia.io.pacworx.ambrosia.models.Hero
import io.pacworx.ambrosia.io.pacworx.ambrosia.models.HeroBaseRepository
import io.pacworx.ambrosia.io.pacworx.ambrosia.models.HeroRepository
import io.pacworx.ambrosia.io.pacworx.ambrosia.services.HeroService
import org.springframework.web.bind.annotation.*

@RestController
@CrossOrigin(maxAge = 3600)
@RequestMapping("hero")
class HeroController(val heroService: HeroService,
                          val heroRepository: HeroRepository,
                          val heroBaseRepository: HeroBaseRepository) {

    @GetMapping("")
    fun getHeroes(): List<Hero> = heroRepository.findAll()

    @PostMapping("recruit/{type}")
    fun recruitHero(@PathVariable type: RecruitType): Hero {
        return when(type) {
            RecruitType.BASIC -> heroService.recruitHero(uncommonChance = 0.02, commonChance = 0.23, default = Rarity.SIMPLE)
            RecruitType.ADVANCED -> heroService.recruitHero(epicChance = 0.005, rareChance = 0.08, default = Rarity.UNCOMMON)
        }
    }

}