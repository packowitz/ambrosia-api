package io.pacworx.ambrosia.io.pacworx.ambrosia.admin.controller

import io.pacworx.ambrosia.io.pacworx.ambrosia.models.Hero
import io.pacworx.ambrosia.io.pacworx.ambrosia.models.HeroBaseRepository
import io.pacworx.ambrosia.io.pacworx.ambrosia.models.HeroRepository
import org.springframework.web.bind.annotation.*

@RestController
@CrossOrigin(maxAge = 3600)
@RequestMapping("admin/hero")
class AdminHeroController(val heroRepository: HeroRepository,
                          val heroBaseRepository: HeroBaseRepository) {

    @PostMapping("recruit/{heroId}")
    fun recruit(@PathVariable heroId: Long): Hero {
        val hero = heroBaseRepository.getOne(heroId).let { Hero(1, it) }
        heroRepository.save(hero)
        return hero
    }

}