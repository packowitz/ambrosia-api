package io.pacworx.ambrosia.battle

import io.pacworx.ambrosia.exceptions.ConfigurationException
import io.pacworx.ambrosia.hero.Color
import io.pacworx.ambrosia.hero.skills.HeroSkill
import io.pacworx.ambrosia.hero.skills.SkillActionType
import io.pacworx.ambrosia.hero.skills.SkillTarget
import mu.KotlinLogging
import org.springframework.stereotype.Service
import kotlin.math.min
import kotlin.math.round

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
            .filter { !it.passive && hero.getSkillLevel(it.number) > 0 && hero.getCooldown(it.number) == 0 && it.target.resolve(battle, hero).isNotEmpty() }
            .maxBy { it.number }
                ?: throw ConfigurationException("Found no skill to use on hero ${hero.heroBase.name} #${hero.heroBase.id}")
    }

    fun findTarget(battle: Battle, hero: BattleHero, skill: HeroSkill): BattleHero {
        val possibleTargets = skill.target.resolve(battle, hero)
        if (possibleTargets.size == 1) {
            return possibleTargets[0]
        }
        if (skill.target in listOf(SkillTarget.OPPONENT, SkillTarget.OPP_IGNORE_TAUNT)) {
            // assuming damage skill
            val expectedBaseDamage = expectedBaseDamage(hero, skill)
            if (expectedBaseDamage > 0) {
                var currentScore = -1
                var currentTarget: BattleHero? = null

                possibleTargets.forEach { target ->
                    var damage = expectedBaseDamage
                    when (target.color) {
                        Color.RED -> damage += (expectedBaseDamage.toDouble() * hero.getTotalRedDamageInc() / 100).toInt()
                        Color.GREEN -> damage += (expectedBaseDamage.toDouble() * hero.getTotalGreenDamageInc() / 100).toInt()
                        Color.BLUE -> damage += (expectedBaseDamage.toDouble() * hero.getTotalBlueDamageInc() / 100).toInt()
                        else -> {}
                    }
                    val shieldSize = target.buffs.filter { it.buff == Buff.SHIELD }.sumBy { it.value ?: 0 }
                    if (shieldSize > damage) {
                        damage /= 2
                    } else {
                        damage -= (shieldSize / 2)
                    }
                    if (damage < target.currentHp || !target.hasDeathshield()) {
                        val score = min(round(10 * damage.toDouble() / target.currentHp).toInt(), 10)
                        if (score > currentScore) {
                            currentScore = score
                            currentTarget = target
                        } else if (score == currentScore) {
                            currentTarget = listOfNotNull(currentTarget, target).maxBy { it.currentSpeedBar }
                        }
                    }
                }
                return currentTarget ?: possibleTargets.random()
            }
        } else if (skill.target == SkillTarget.ALL_OWN) {
            // assuming healing skill
            return possibleTargets.minBy { it.currentHp.toDouble() / it.heroHp } ?: possibleTargets.random()
        }

        return possibleTargets.random()
    }

    private fun expectedBaseDamage(hero: BattleHero, skill: HeroSkill): Int {
        var baseDamage = 0
        skill.actions.forEach { action ->
            if (action.type == SkillActionType.ADD_BASE_DMG && skillService.actionTriggers(hero, skill, action)) {
                baseDamage += skillService.handleDefineDamageAction(hero, action, baseDamage)
            }
        }
        return baseDamage
    }
}
