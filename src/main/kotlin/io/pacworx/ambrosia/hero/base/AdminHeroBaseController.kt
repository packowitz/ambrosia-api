package io.pacworx.ambrosia.hero.base

import io.pacworx.ambrosia.battle.BattleRepository
import io.pacworx.ambrosia.battle.BattleService
import io.pacworx.ambrosia.battle.offline.MissionRepository
import io.pacworx.ambrosia.fights.FightStageRepository
import io.pacworx.ambrosia.hero.HeroRepository
import io.pacworx.ambrosia.vehicle.VehicleRepository
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import javax.transaction.Transactional
import javax.validation.Valid

@RestController
@CrossOrigin(maxAge = 3600)
@RequestMapping("admin/hero_base")
class AdminHeroBaseController(val heroBaseRepository: HeroBaseRepository,
                              val heroRepository: HeroRepository,
                              val fightStageRepository: FightStageRepository,
                              val battleRepository: BattleRepository,
                              val battleService: BattleService,
                              val missionRepository: MissionRepository,
                              val vehicleRepository: VehicleRepository) {

    @GetMapping("")
    fun getHeroBases(): List<HeroBase> = heroBaseRepository.findAll()

    @GetMapping("{id}")
    fun getHeroBase(@PathVariable id: Long): HeroBase = heroBaseRepository.getOne(id)

    @PostMapping("")
    @Transactional
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
    @Transactional
    fun updateHeroBase(@PathVariable id: Long, @RequestBody @Valid heroBaseRequest: HeroBase): HeroBase {
        return heroBaseRepository.save(heroBaseRequest)
    }

    @DeleteMapping("{id}")
    @Transactional
    fun deleteHeroBase(@PathVariable id: Long) {
        val heroBase = heroBaseRepository.getOne(id)
        heroRepository.findAllByHeroBase(heroBase).forEach { hero ->
            fightStageRepository.findStagesContainingHero(hero.id).takeIf { it.isNotEmpty() }?.let {
                throw RuntimeException("BaseHero #$id cannot get deleted as there are fights configured using it. Fight ids: ${it.distinct().joinToString()}")
            }
            battleRepository.findAllByContainingHero(hero.id).forEach { battleId ->
                battleService.deleteBattle(battleRepository.getOne(battleId))
            }
            missionRepository.findAllByContainingHero(hero.id).forEach { missionId ->
                val mission = missionRepository.getOne(missionId)
                val vehicle = vehicleRepository.getOne(mission.vehicleId)
                heroRepository.findAllById(listOfNotNull(
                    mission.hero1Id?.takeIf { it != hero.id },
                    mission.hero2Id?.takeIf { it != hero.id },
                    mission.hero3Id?.takeIf { it != hero.id },
                    mission.hero4Id?.takeIf { it != hero.id }
                )).forEach { it.missionId = null }
                vehicle.missionId = null
                missionRepository.delete(mission)
            }
            hero.unequipAll()
            heroRepository.delete(hero)
        }
        heroBaseRepository.delete(heroBase)
    }
}
