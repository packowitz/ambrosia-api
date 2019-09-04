package io.pacworx.ambrosia.io.pacworx.ambrosia.battle

import io.pacworx.ambrosia.io.pacworx.ambrosia.models.HeroSkill
import io.pacworx.ambrosia.io.pacworx.ambrosia.services.PropertyService
import org.springframework.stereotype.Service

@Service
class SkillService(private val propertyService: PropertyService) {

    private val SPEEDBAR_REDUCTION: Int = 10000

    fun useSkill(battle: Battle, hero: BattleHero, skill: HeroSkill, target: BattleHero) {

        hero.currentSpeedBar -= SPEEDBAR_REDUCTION
    }
}
