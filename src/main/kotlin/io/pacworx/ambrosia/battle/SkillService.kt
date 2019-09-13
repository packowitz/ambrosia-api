package io.pacworx.ambrosia.io.pacworx.ambrosia.battle

import io.pacworx.ambrosia.io.pacworx.ambrosia.enums.*
import io.pacworx.ambrosia.io.pacworx.ambrosia.models.DynamicProperty
import io.pacworx.ambrosia.io.pacworx.ambrosia.models.HeroSkill
import io.pacworx.ambrosia.io.pacworx.ambrosia.models.HeroSkillAction
import io.pacworx.ambrosia.io.pacworx.ambrosia.services.PropertyService
import org.springframework.stereotype.Service
import java.lang.RuntimeException
import kotlin.math.min
import kotlin.random.Random

@Service
class SkillService(private val propertyService: PropertyService) {

    companion object {
        private const val SPEEDBAR_MAX: Int = 10000
    }

    var battleProps = listOf<DynamicProperty>()

    fun initProps() {
        battleProps = propertyService.getAllProperties(PropertyType.BATTLE_ARMOR)
    }

    fun useSkill(battle: Battle, hero: BattleHero, skill: HeroSkill, target: BattleHero) {
        if (!isTargetEligible(battle, hero, skill, target)) {
            throw RuntimeException("Target ${target.position} is not valid target for skill ${skill.number} of hero ${hero.position} in battle ${battle.id}")
        }
        initProps()
        val step = BattleStep(
                turn = battle.turnsDone,
                actingHero = hero.position,
                usedSkill = skill.number,
                target = target.position
        )
        battle.steps.add(step)
        var damage = 0
        skill.actions.forEach { action ->
            if (actionTriggers(hero, action, step) && procs(action.triggerChance)) {
                when (action.type) {
                    SkillActionType.DAMAGE -> damage += handleDamageAction(hero, action, damage)
                    SkillActionType.DEAL_DAMAGE ->
                        findTargets(battle, hero, action, target)
                                .forEach {
                                    step.addAction(dealDamage(it, hero, action, damage))
                                }
                    SkillActionType.BUFF, SkillActionType.DEBUFF ->
                        findTargets(battle, hero, action, target)
                                .forEach {
                                    step.addAction(applyBuff(battle, hero, action, it))
                                }
                    SkillActionType.SPEEDBAR ->
                        findTargets(battle, hero, action, target)
                                .forEach {
                                    applySpeedbarAction(it, action)
                                }
                    SkillActionType.HEAL ->
                        findTargets(battle, hero, action, target)
                                .forEach {
                                    step.addAction(applyHealingAction(hero, action, it))
                                }
                }
            }
        }
        hero.currentSpeedBar -= SPEEDBAR_MAX
        hero.skillUsed(skill.number)
        hero.afterTurn(battle, propertyService)
        battle.checkStatus()
    }

    private fun isTargetEligible(battle: Battle, hero: BattleHero, skill: HeroSkill, target: BattleHero): Boolean {
        val isPlayer = battle.heroBelongsToPlayer(hero)
        return when(skill.target) {
            SkillTarget.OPPONENT -> isPlayer == battle.heroBelongsToOpponent(target)
            SkillTarget.SELF -> hero.position == target.position
            SkillTarget.ALL_OWN -> isPlayer == battle.heroBelongsToPlayer(target)
            SkillTarget.OPP_IGNORE_TAUNT -> isPlayer == battle.heroBelongsToOpponent(target)
            SkillTarget.DEAD -> isPlayer == battle.heroBelongsToPlayer(target) && target.status == HeroStatus.DEAD
        }
    }

    private fun actionTriggers(hero: BattleHero, action: HeroSkillAction, step: BattleStep): Boolean {
        return when(action.trigger) {
            SkillActionTrigger.ALWAYS -> true
            SkillActionTrigger.S1_LVL -> action.triggerValue!!.contains(hero.skill1Lvl.toString())
            SkillActionTrigger.S2_LVL -> action.triggerValue!!.contains(hero.skill2Lvl.toString())
            SkillActionTrigger.S3_LVL -> action.triggerValue!!.contains(hero.skill3Lvl.toString())
            SkillActionTrigger.S4_LVL -> action.triggerValue!!.contains(hero.skill4Lvl.toString())
            SkillActionTrigger.S5_LVL -> action.triggerValue!!.contains(hero.skill5Lvl.toString())
            SkillActionTrigger.S6_LVL -> action.triggerValue!!.contains(hero.skill6Lvl.toString())
            SkillActionTrigger.S7_LVL -> action.triggerValue!!.contains(hero.skill7Lvl.toString())
            SkillActionTrigger.ANY_CRIT_DMG -> step.actions.any { it.crit == true }
            SkillActionTrigger.DMG_OVER -> step.actions.sumBy { it.healthDiff ?: 0 } > action.triggerValue!!.toInt()
            SkillActionTrigger.ASCENDED -> hero.ascLvl > 0
        }
    }

    private fun procs(chance: Int): Boolean {
        if (chance >= 100) {
            return true
        }
        if (chance <= 0) {
            return false
        }
        return Random.nextInt(100) < chance
    }

    private fun findTargets(battle: Battle, hero: BattleHero, action: HeroSkillAction, target: BattleHero): List<BattleHero> {
        return when(action.target) {
            SkillActionTarget.TARGET -> listOf(target)
            SkillActionTarget.ALL_OPP -> battle.allOtherHeroesAlive(hero)
            SkillActionTarget.RANDOM_OPP -> listOf(battle.allOtherHeroesAlive(hero).random())
            SkillActionTarget.RANDOM_OTHER_OPP -> {
                listOfNotNull(
                        battle.allOtherHeroesAlive(hero)
                                .filter { it.position != target.position }
                                .takeIf { it.isNotEmpty() }
                                ?.random()
                )
            }
            SkillActionTarget.SELF -> listOf(hero)
            SkillActionTarget.ALL_ALLIES -> battle.allAlliedHeroesDead(hero)
            SkillActionTarget.ALL_OTHER_ALLIES ->
                battle.allAlliedHeroesAlive(hero).filter { it.position != hero.position }
        }
    }

    private fun handleDamageAction(hero: BattleHero, action: HeroSkillAction, damage: Int): Int {
        return when(action.effect) {
            SkillActionEffect.STRENGTH -> (hero.getTotalStrength() * action.effectValue) / 100
            SkillActionEffect.ARMOR -> (hero.getTotalArmor() * action.effectValue) / 100
            SkillActionEffect.ARMOR_MAX -> (hero.getTotalMaxArmor() * action.effectValue) / 100
            SkillActionEffect.HP -> (hero.currentHp * action.effectValue) / 100
            SkillActionEffect.HP_MAX -> (hero.heroHp * action.effectValue) / 100
            SkillActionEffect.DEXTERITY -> (hero.getTotalDexterity() * action.effectValue) / 100
            SkillActionEffect.RESISTANCE -> (hero.getTotalResistance() * action.effectValue) / 100
            SkillActionEffect.MULTIPLIER -> (damage * action.effectValue) / 100
            else -> 0
        }
    }

    private fun dealDamage(hero: BattleHero, damageDealer: BattleHero, action: HeroSkillAction, baseDamage: Int): BattleStepAction {
        val crit = Random.nextInt(100) < hero.getTotalCrit()
        val superCrit = crit && Random.nextInt(100) < hero.heroSuperCritChance + hero.superCritChanceBonus

        var damage = (baseDamage * action.effectValue) / 100
        if (crit && !superCrit) {
            damage += ((damage * hero.getTotalCritMult()) / 100)
        } else if (superCrit) {
            damage += ((damage * (hero.getTotalCritMult() + 150)) / 100)
        }

        val dmgArmorRatio: Int = 100 * damage / hero.getTotalArmor()
        val property = battleProps.find { dmgArmorRatio <= it.level!! } ?: battleProps.last()

        val armorLoss = (hero.currentArmor * property.value1) / 100
        val healthLoss = (damage * property.value2!!) / 100

        hero.currentArmor -= armorLoss
        hero.currentHp -= healthLoss

        if (hero.currentHp <= 0) {
            hero.status = HeroStatus.DEAD
        }

        return BattleStepAction(
                heroPosition = hero.position,
                crit = crit,
                superCrit = superCrit,
                armorDiff = -armorLoss,
                healthDiff = -healthLoss
        )
    }

    private fun applyBuff(battle: Battle, hero: BattleHero, action: HeroSkillAction, target: BattleHero): BattleStepAction {
        val buff = Buff.valueOf(action.effect.name)
        var intesity = action.effectValue
        if (action.type == SkillActionType.BUFF) {
            intesity += hero.buffIntensityIncBonus
        }
        if (action.type == SkillActionType.DEBUFF) {
            intesity += hero.debuffIntensityIncBonus
        }
        val duration = action.effectDuration!!

        var resisted = false
        if (buff.type == BuffType.DEBUFF) {
            //TODO
        }

        if (!resisted) {
            target.buffs.add(BattleHeroBuff(
                    buff = buff,
                    intensity = intesity,
                    duration = duration,
                    sourceHeroId = hero.id
            ))
            target.resetBonus(battle, propertyService)
        }

        return BattleStepAction(
                heroPosition = target.position,
                buff = buff,
                buffResisted = resisted,
                buffIntensity = intesity,
                buffDuration = duration
        )
    }

    private fun applySpeedbarAction(hero: BattleHero, action: HeroSkillAction) {
        when(action.effect) {
            SkillActionEffect.PERCENTAGE -> hero.currentSpeedBar += (SPEEDBAR_MAX * action.effectValue) / 100
            else -> {}
        }
    }

    private fun applyHealingAction(hero: BattleHero, action: HeroSkillAction, target: BattleHero): BattleStepAction {
        var healing = when (action.effect) {
            SkillActionEffect.TARGET_MAX_HP -> (target.heroHp * action.effectValue) / 100
            SkillActionEffect.OWN_MAX_HP -> (hero.heroHp * action.effectValue) / 100
            else -> 0
        }
        healing += ((hero.heroHealingInc + hero.healingIncBonus) * healing) / 100
        healing = min(healing, target.heroHp - target.currentHp)

        hero.currentHp += healing

        return BattleStepAction(heroPosition = target.position, healthDiff = healing)
    }
}