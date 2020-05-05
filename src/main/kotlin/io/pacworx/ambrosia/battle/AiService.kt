package io.pacworx.ambrosia.battle

import io.pacworx.ambrosia.hero.skills.HeroSkill
import mu.KotlinLogging
import org.springframework.stereotype.Service

@Service
class AiService(private val skillService: SkillService) {
    private val log = KotlinLogging.logger {}

    fun doAction(battle: Battle, hero: BattleHero) {
        val skill = findSkillToUse(battle, hero)
        val target = findTarget(battle, hero, skill)
        log.info("Battle ${battle.id} turn ${battle.turnsDone} AI: hero ${hero.position} uses S${skill.number} on ${target.position}")
        skillService.useSkill(battle, hero, skill, target)
    }

    fun findSkillToUse(battle: Battle, hero: BattleHero): HeroSkill {
        return hero.heroBase.skills
            .filter { !it.passive && hero.getCooldown(it.number) == 0 && it.target.resolve(battle, hero).isNotEmpty() }
            .maxBy { it.number }
                ?: throw RuntimeException("Found no skill to use")
    }

    fun findTarget(battle: Battle, hero: BattleHero, skill: HeroSkill): BattleHero {
        return skill.target.resolve(battle, hero).random()
    }
}
