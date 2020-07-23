package io.pacworx.ambrosia.hero

import org.springframework.web.bind.annotation.*

@RestController
@CrossOrigin(maxAge = 3600)
@RequestMapping("hero_base")
class HeroBaseController(private val heroBaseRepository: HeroBaseRepository) {

    @GetMapping("")
    fun getHeroBases(): List<HeroBase> = heroBaseRepository.findAll()

    @GetMapping("{id}")
    fun getHeroBase(@PathVariable id: Long): HeroBase = heroBaseRepository.getOne(id)
}
