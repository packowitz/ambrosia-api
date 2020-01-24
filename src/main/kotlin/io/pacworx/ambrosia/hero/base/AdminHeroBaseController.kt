package io.pacworx.ambrosia.hero.base

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import javax.validation.Valid

@RestController
@CrossOrigin(maxAge = 3600)
@RequestMapping("admin/hero_base")
class AdminHeroBaseController(val heroBaseRepository: HeroBaseRepository) {

    @GetMapping("")
    fun getHeroBases(): List<HeroBase> = heroBaseRepository.findAll()

    @GetMapping("{id}")
    fun getHeroBase(@PathVariable id: Long): HeroBase = heroBaseRepository.getOne(id)

    @PostMapping("")
    fun postHeroBase(@RequestBody @Valid heroBaseRequest: HeroBase): HeroBase {
        val heroClass = heroBaseRequest.heroClass
        val rarity = heroBaseRequest.rarity
        val color = heroBaseRequest.color
        heroBaseRepository.findByHeroClassAndRarityAndColor(heroClass, rarity, color)?.let {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "$rarity $color hero of $heroClass already exists")
        }
        return heroBaseRepository.save(heroBaseRequest)
    }

    @PutMapping("{id}")
    fun updateHeroBase(@PathVariable id: Long, @RequestBody @Valid heroBaseRequest: HeroBase): HeroBase {
        return heroBaseRepository.save(heroBaseRequest)
    }

    @DeleteMapping("{id}")
    fun deleteHeroBase(@PathVariable id: Long) {
        heroBaseRepository.deleteById(id)
    }
}
