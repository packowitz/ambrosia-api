package io.pacworx.ambrosia.battle

import io.pacworx.ambrosia.battle.BattleService.Companion.SPEEDBAR_MAX
import io.pacworx.ambrosia.common.procs
import io.pacworx.ambrosia.exceptions.ConfigurationException
import io.pacworx.ambrosia.hero.skills.SkillActionEffect.*
import io.pacworx.ambrosia.hero.Color
import io.pacworx.ambrosia.hero.skills.HeroSkill
import io.pacworx.ambrosia.hero.skills.HeroSkillAction
import io.pacworx.ambrosia.hero.HeroStat
import io.pacworx.ambrosia.hero.skills.PassiveSkillTrigger
import io.pacworx.ambrosia.hero.skills.SkillActionTarget
import io.pacworx.ambrosia.hero.skills.SkillActionTrigger
import io.pacworx.ambrosia.hero.skills.SkillActionType
import io.pacworx.ambrosia.properties.DynamicProperty
import io.pacworx.ambrosia.properties.PropertyService
import io.pacworx.ambrosia.properties.PropertyType
import org.springframework.stereotype.Service
import kotlin.math.max
import kotlin.math.min
import kotlin.math.round

@Service
class SkillService(private val propertyService: PropertyService) {

    companion object {
        private val hero_positions = listOf(HeroPosition.HERO1, HeroPosition.HERO2, HeroPosition.HERO3, HeroPosition.HERO4)
        private val opp_positions = listOf(HeroPosition.OPP1, HeroPosition.OPP2, HeroPosition.OPP3, HeroPosition.OPP4)
    }

    var battleProps = listOf<DynamicProperty>()

    fun initProps() {
        battleProps = propertyService.getAllProperties(PropertyType.BATTLE_ARMOR)
    }

    fun useSkill(battle: Battle, hero: BattleHero, skill: HeroSkill, target: BattleHero) {
        initProps()
        battle.applyBonuses(propertyService)
        val step = BattleStep(
            battleId = battle.id,
            turn = battle.turnsDone,
            phase = BattleStepPhase.MAIN,
            actingHero = hero.position,
            actingHeroName = hero.heroBase.name,
            usedSkill = skill.number,
            usedSkillName = skill.name,
            target = target.position,
            targetName = target.heroBase.name,
            heroStates = battle.getBattleStepHeroStates()
        )
        battle.addStep(step)

        hero.skillUsed(skill.number)
        executeSkillActions(battle, step, hero, skill, target)

        // check for counter attacks
        battle.allOtherHeroesAlive(hero).filter { it.willCounter }.forEach { counterHero ->
            if (hero.status != HeroStatus.DEAD) {
                doCounter(battle, counterHero, hero, BattleStepPhase.Z_COUNTER)
            }
        }

        hero.afterTurn(battle, propertyService)
        battle.checkStatus()
    }

    private fun executeSkillActions(battle: Battle, step: BattleStep, hero: BattleHero, skill: HeroSkill, target: BattleHero): Boolean {
        var damage = 0
        var lastActionProced: Boolean? = null
        var skillDidSomething = false
        skill.actions.forEach { action ->
            if (!actionTriggers(hero, skill, action, step, lastActionProced)) {
                lastActionProced = null
                return@forEach
            }
            if (!procs(action.triggerChance)) {
                lastActionProced = false
                return@forEach
            }
            lastActionProced = true
            when (action.type) {
                SkillActionType.ADD_BASE_DMG -> damage += handleDefineDamageAction(hero, action, damage, step)
                SkillActionType.DEAL_DAMAGE ->
                    findTargets(battle, hero, action, target)
                        .forEach {
                            dealDamage(battle, it, hero, action, damage, step)
                            skillDidSomething = true
                        }
                SkillActionType.BUFF, SkillActionType.DEBUFF ->
                    findTargets(battle, hero, action, target)
                        .forEach {
                            step.addAction(applyBuff(battle, hero, action, it))
                            skillDidSomething = true
                        }
                SkillActionType.SPEEDBAR ->
                    findTargets(battle, hero, action, target)
                        .forEach {
                            applySpeedbarAction(step, it, action)
                            skillDidSomething = true
                        }
                SkillActionType.HEAL ->
                    findTargets(battle, hero, action, target)
                        .forEach {
                            step.addAction(applyHealingAction(battle, hero, action, it))
                            skillDidSomething = true
                        }
                SkillActionType.PASSIVE_STAT ->
                    findTargets(battle, hero, action, target)
                        .forEach {
                            action.effect.stat?.apply(it, action.effectValue)
                        }
                SkillActionType.SPECIAL ->
                    findTargets(battle, hero, action, target)
                        .forEach {
                            skillDidSomething = applySpecialAction(battle, step, hero, action, it) || skillDidSomething
                        }
            }
        }
        return skillDidSomething
    }

    fun actionTriggers(hero: BattleHero, skill: HeroSkill, action: HeroSkillAction, step: BattleStep? = null, lastActionProced: Boolean? = null): Boolean {
        return when (action.trigger) {
            SkillActionTrigger.ALWAYS -> true
            SkillActionTrigger.SKILL_LVL -> triggerValueSkillLevel(action.triggerValue!!, hero.getSkillLevel(skill.number))
            SkillActionTrigger.S1_LVL -> action.triggerValue!!.contains(hero.skill1Lvl.toString())
            SkillActionTrigger.S2_LVL -> hero.skill2Lvl?.let { triggerValueSkillLevel(action.triggerValue!!, it) } ?: false
            SkillActionTrigger.S3_LVL -> hero.skill3Lvl?.let { triggerValueSkillLevel(action.triggerValue!!, it) } ?: false
            SkillActionTrigger.S4_LVL -> hero.skill4Lvl?.let { triggerValueSkillLevel(action.triggerValue!!, it) } ?: false
            SkillActionTrigger.S5_LVL -> hero.skill5Lvl?.let { triggerValueSkillLevel(action.triggerValue!!, it) } ?: false
            SkillActionTrigger.S6_LVL -> hero.skill6Lvl?.let { triggerValueSkillLevel(action.triggerValue!!, it) } ?: false
            SkillActionTrigger.S7_LVL -> hero.skill7Lvl?.let { triggerValueSkillLevel(action.triggerValue!!, it) } ?: false
            SkillActionTrigger.PREV_ACTION_PROCED -> lastActionProced == true
            SkillActionTrigger.PREV_ACTION_NOT_PROCED -> lastActionProced == false
            SkillActionTrigger.ANY_CRIT_DMG -> step?.actions?.any { it.crit == true } ?: false
            SkillActionTrigger.DMG_OVER -> step?.actions?.sumBy { it.healthDiff ?: 0 } ?: 0 > action.triggerValue!!.toInt()
            SkillActionTrigger.ASC_LVL -> if (action.triggerValue == null) { hero.ascLvl > 0 } else { action.triggerValue!!.split(",").contains(hero.ascLvl.toString()) }
        }
    }

    private fun triggerValueSkillLevel(triggerValue: String, skillLevel: Int): Boolean {
        return if (triggerValue.startsWith(">=")) {
            skillLevel >= triggerValue.substring(2).trim().toIntOrNull() ?: 99
        } else if (triggerValue.startsWith(">")) {
            skillLevel > triggerValue.substring(1).trim().toIntOrNull() ?: 99
        } else if (triggerValue.startsWith("<=")) {
            skillLevel <= triggerValue.substring(2).trim().toIntOrNull() ?: 99
        } else if (triggerValue.startsWith("<")) {
            skillLevel < triggerValue.substring(1).trim().toIntOrNull() ?: 99
        } else {
            triggerValue.contains(skillLevel.toString())
        }
    }

    private fun findTargets(battle: Battle, hero: BattleHero, action: HeroSkillAction, target: BattleHero): List<BattleHero> {
        return when (action.target) {
            SkillActionTarget.TARGET -> listOf(target)
            SkillActionTarget.ALL_OPP -> battle.allOtherHeroesAlive(hero)
            SkillActionTarget.ALL_OTHER_OPP -> battle.allOtherHeroesAlive(hero).filter { it.position != target.position }
            SkillActionTarget.RANDOM_OPP -> listOfNotNull(battle.allOtherHeroesAlive(hero).takeIf { it.isNotEmpty() }?.random())
            SkillActionTarget.RANDOM_OTHER_OPP -> {
                listOfNotNull(
                        battle.allOtherHeroesAlive(hero)
                                .filter { it.position != target.position }
                                .takeIf { it.isNotEmpty() }
                                ?.random()
                )
            }
            SkillActionTarget.SELF -> listOf(hero)
            SkillActionTarget.ALL_ALLIES -> battle.allAlliedHeroesAlive(hero)
            SkillActionTarget.ALL_OTHER_ALLIES ->
                battle.allAlliedHeroesAlive(hero).filter { it.position != hero.position }
            SkillActionTarget.RANDOM_ALLY -> listOfNotNull(battle.allAlliedHeroesAlive(hero).takeIf { it.isNotEmpty() }?.random())
            SkillActionTarget.RANDOM_OTHER_ALLY ->
                listOfNotNull(battle.allAlliedHeroesAlive(hero).filter { it.position != hero.position }.takeIf { it.isNotEmpty() }?.random())
            SkillActionTarget.ALLY_LOWEST_HP ->
                listOfNotNull(battle.allAlliedHeroesAlive(hero).shuffled().minBy { it.currentHp.toDouble() / it.heroHp })
            else -> emptyList()
        }
    }

    fun handleDefineDamageAction(hero: BattleHero, action: HeroSkillAction, damage: Int, step: BattleStep? = null): Int {
        var dmgSource: String? = null
        return when (action.effect) {
            STRENGTH_SCALING -> ((hero.getTotalStrength() * action.effectValue) / 100).also { dmgSource = "STR" }
            ARMOR_SCALING -> ((hero.getTotalArmor() * action.effectValue) / 100).also { dmgSource = "ARM" }
            ARMOR_MAX_SCALING -> ((hero.getTotalMaxArmor() * action.effectValue) / 100).also { dmgSource = "ARM_M" }
            HP_SCALING -> ((hero.currentHp * action.effectValue) / 100).also { dmgSource = "HP" }
            HP_MAX_SCALING -> ((hero.heroHp * action.effectValue) / 100).also { dmgSource = "HP_M" }
            DEXTERITY_SCALING -> ((hero.getTotalDexterity() * action.effectValue) / 100).also { dmgSource = "DEX" }
            RESISTANCE_SCALING -> ((hero.getTotalResistance() * action.effectValue) / 100).also { dmgSource = "RES" }
            HERO_LVL_SCALING -> ((hero.level * action.effectValue) / 100).also { dmgSource = "LVL" }
            DMG_MULTIPLIER -> ((damage * action.effectValue) / 100).also { dmgSource = "MULT" }
            FIXED_DMG -> (action.effectValue).also { dmgSource = "FIX" }
            else -> 0
        }.also { step?.addBaseDamageText("+$it ($dmgSource)") }
    }

    private fun dealDamage(battle: Battle, target: BattleHero, damageDealer: BattleHero, action: HeroSkillAction, incomingDamage: Int, step: BattleStep, isFreeAttack: Boolean = false) {
        if (procs(target.getTotalDodgeChance())) {
            step.addAction(BattleStepAction(
                    heroPosition = target.position,
                    heroName = target.heroBase.name,
                    type = BattleStepActionType.DODGED
            ))
            return
        }

        if (!isFreeAttack && areOpponents(target, damageDealer)) {
            target.willCounter = target.willCounter || procs(target.getTotalCounterChance())
        }

        var baseDamageText = step.baseDamageText ?: "0"

        val crit = procs(damageDealer.getTotalCrit())
        val superCrit = crit && procs(damageDealer.heroSuperCritChance + damageDealer.superCritChanceBonus)

        var baseDamageDouble = if (action.effectValue == 100) {
            incomingDamage.toDouble()
        } else {
            ((incomingDamage * action.effectValue).toDouble() / 100).also {
                baseDamageText += if (it >= incomingDamage) { " +" } else { " " }
                baseDamageText += "${it - incomingDamage} (EFF)"
            }
        }
        if (superCrit) {
            baseDamageDouble += ((baseDamageDouble * (target.getTotalCritMult() + 150)) / 100).also { baseDamageText += " +$it (SUP_CR)" }
        } else if (crit) {
            baseDamageDouble += ((baseDamageDouble * target.getTotalCritMult()) / 100).also { baseDamageText += " +$it (CR)" }
        }

        val reflectDamage = round(target.getTotalReflect().takeIf { it > 0 }?.let { (baseDamageDouble * it) / 100 } ?: 0.0).toInt()
        baseDamageDouble -= reflectDamage.also { if (it > 0) { baseDamageText += " -$it (REFLECT)" } }

        baseDamageDouble += when (target.color) {
            Color.RED -> baseDamageDouble * damageDealer.getTotalRedDamageInc() / 100
            Color.GREEN -> baseDamageDouble * damageDealer.getTotalGreenDamageInc() / 100
            Color.BLUE -> baseDamageDouble * damageDealer.getTotalBlueDamageInc() / 100
            else -> 0.0
        }.also {
            if (it != 0.0) {
                baseDamageText += if (it > 0.0) { " +" } else { " " }
                baseDamageText += "$it (COL)"
            }
        }

        if (battle.heroBelongsToPlayer(target)) {
            baseDamageDouble += when (target.color) {
                Color.RED -> baseDamageDouble * (battle.fight?.environment?.playerRedDmgInc ?: 0) / 100
                Color.GREEN -> baseDamageDouble * (battle.fight?.environment?.playerGreenDmgInc ?: 0) / 100
                Color.BLUE -> baseDamageDouble * (battle.fight?.environment?.playerBlueDmgInc ?: 0) / 100
                else -> 0.0
            }.also {
                if (it != 0.0) {
                    baseDamageText += if (it > 0.0) { " +" } else { " " }
                    baseDamageText += "$it (ENV_COL)"
                }
            }
        } else {
            baseDamageDouble -= when (target.color) {
                Color.RED -> baseDamageDouble * (battle.fight?.environment?.oppRedDmgDec ?: 0) / 100
                Color.GREEN -> baseDamageDouble * (battle.fight?.environment?.oppGreenDmgDec ?: 0) / 100
                Color.BLUE -> baseDamageDouble * (battle.fight?.environment?.oppBlueDmgDec ?: 0) / 100
                else -> 0.0
            }.also {
                if (it != 0.0) {
                    baseDamageText += if (it > 0.0) { " " } else { " +" }
                    baseDamageText += "${it * -1} (ENV_COL)"
                }
            }
        }

        val baseDamage = round(baseDamageDouble).toInt()
        val damage = shieldCalculation(target, baseDamage)

        val armorPiercedDamage = round(damageDealer.getTotalArmorPiercing().takeIf { it > 0 }?.let { (damage * it).toDouble() / 100 } ?: 0.0).toInt()

        val targetArmor = target.getTotalArmor()
        val targetHealth = target.currentHp
        val property = if (targetArmor > 0) {
            val dmgArmorRatio: Int = round(100 * (damage - armorPiercedDamage).toDouble() / targetArmor).toInt()
            battleProps.find { dmgArmorRatio <= it.level!! } ?: battleProps.last()
        } else {
            battleProps.last()
        }

        var armorLoss = round((target.currentArmor * property.value1).toDouble() / 100).toInt()
        armorLoss = round(armorLoss * (100 + damageDealer.getTotalArmorExtraDamage()).toDouble() / 100).toInt()
        armorLoss = min(armorLoss, targetArmor)
        var healthLoss = armorPiercedDamage + round(((damage - armorPiercedDamage) * property.value2!!).toDouble() / 100).toInt()
        healthLoss = round(healthLoss * (100 + damageDealer.getTotalHealthExtraDamage()).toDouble() / 100).toInt()

        receiveDamage(battle, target, armorLoss, healthLoss, damageDealer)

        val lifeSteal = round(damageDealer.getTotalLifesteal().takeIf { it > 0 }?.let { (healthLoss * it).toDouble() / 100 } ?: 0.0).toInt()

        step.addAction(BattleStepAction(
            heroPosition = target.position,
            heroName = target.heroBase.name,
            type = BattleStepActionType.DAMAGE,
            crit = crit,
            superCrit = superCrit,
            baseDamage = baseDamage,
            baseDamageText = baseDamageText,
            shieldAbsorb = baseDamage - damage,
            targetArmor = targetArmor,
            targetHealth = targetHealth,
            armorDiff = -armorLoss,
            healthDiff = -healthLoss
        ))

        if (target.status == HeroStatus.DEAD) {
            step.addAction(BattleStepAction(
                    heroPosition = target.position,
                    heroName = target.heroBase.name,
                    type = BattleStepActionType.DEAD))
        }

        if (lifeSteal > 0 && damageDealer.currentHp < damageDealer.heroHp) {
            val maxHealing = max(damageDealer.heroHp - damageDealer.currentHp, 0)
            var healing = round(lifeSteal * (100 + damageDealer.getTotalHealingInc()).toDouble() / 100).toInt()
            healing = min(healing, maxHealing)
            damageDealer.currentHp += healing
            step.addAction(BattleStepAction(heroPosition = damageDealer.position, heroName = damageDealer.heroBase.name, type = BattleStepActionType.HEALING, healthDiff = healing))
        }

        if (reflectDamage > 0) {
            applyReflectDamage(battle, damageDealer, step, reflectDamage)
        }
    }

    private fun doCounter(battle: Battle, counterHero: BattleHero, hero: BattleHero, phase: BattleStepPhase) {
        var damage = 0
        var lastActionProced: Boolean? = null
        counterHero.heroBase.skills.find { it.number == 1 }?.let { skill ->
            val step = BattleStep(
                battleId = battle.id,
                turn = battle.turnsDone,
                phase = phase,
                actingHero = counterHero.position,
                actingHeroName = counterHero.heroBase.name,
                usedSkill = skill.number,
                usedSkillName = skill.name,
                target = hero.position,
                targetName = hero.heroBase.name,
                heroStates = battle.getBattleStepHeroStates()
            )
            battle.addStep(step)
            skill.actions.forEach { action ->
                if (!actionTriggers(hero, skill, action, step, lastActionProced)) {
                    lastActionProced = null
                    return@forEach
                }
                if (!procs(action.triggerChance)) {
                    lastActionProced = false
                    return@forEach
                }
                lastActionProced = true
                when (action.type) {
                    SkillActionType.ADD_BASE_DMG -> damage += handleDefineDamageAction(counterHero, action, damage, step)
                    SkillActionType.DEAL_DAMAGE ->
                        findTargets(battle, counterHero, action, hero)
                                .forEach {
                                    dealDamage(battle, it, counterHero, action, damage, step, true)
                                }
                    SkillActionType.HEAL ->
                        findTargets(battle, counterHero, action, hero)
                                .forEach {
                                    step.addAction(applyHealingAction(battle, counterHero, action, it))
                                }
                    SkillActionType.PASSIVE_STAT ->
                        findTargets(battle, counterHero, action, hero)
                                .forEach {
                                    action.effect.stat?.apply(it, action.effectValue)
                                }
                    SkillActionType.SPECIAL ->
                        findTargets(battle, counterHero, action, hero)
                                .forEach {
                                    applySpecialAction(battle, step, counterHero, action, it)
                                }
                    else -> {}
                }
            }
        }
    }

    private fun applyReflectDamage(battle: Battle, hero: BattleHero, step: BattleStep, reflectDamage: Int) {
        val targetArmor = hero.getTotalArmor()
        val targetHealth = hero.currentHp
        val damage = shieldCalculation(hero, reflectDamage)
//        val dmgArmorRatio: Int = 100 * damage / targetArmor
//        val property = battleProps.find { dmgArmorRatio <= it.level!! } ?: battleProps.last()
//
//        val armorLoss = (hero.currentArmor * property.value1) / 100
//        val healthLoss = (damage * property.value2!!) / 100

        // try out that reflect ignores armor
        val armorLoss = 0
        val healthLoss = damage

        receiveDamage(battle, hero, armorLoss, healthLoss)

        step.addAction(BattleStepAction(
                heroPosition = hero.position,
                heroName = hero.heroBase.name,
                type = BattleStepActionType.DAMAGE,
                crit = false,
                superCrit = false,
                baseDamage = reflectDamage,
                targetArmor = targetArmor,
                targetHealth = targetHealth,
                armorDiff = -armorLoss,
                healthDiff = -healthLoss,
                shieldAbsorb = reflectDamage - damage
        ))

        if (hero.status == HeroStatus.DEAD) {
            step.addAction(BattleStepAction(
                    heroPosition = hero.position,
                    heroName = hero.heroBase.name,
                    type = BattleStepActionType.DEAD))
        }
    }

    fun shieldCalculation(hero: BattleHero, baseDamage: Int): Int {
        var damage = baseDamage
        while (damage > 0 && hero.getShield() != null) {
            val shield = hero.getShield()!!
            if (shield.value!! > damage) {
                shield.value = shield.value!! - damage
                damage = 0
            } else {
                damage -= shield.value!!
                hero.buffs.remove(shield)
            }
        }
        return damage
    }

    fun receiveDamage(battle: Battle, hero: BattleHero, armorLoss: Int, healthLoss: Int, executer: BattleHero? = null) {
        hero.currentArmor -= armorLoss
        hero.currentHp -= healthLoss
        if (hero.currentHp <= 0 && hero.hasDeathshield()) {
            hero.currentHp = 1
        }
        if (hero.currentHp <= 0) {
            hero.status = HeroStatus.DEAD
            hero.currentHp = 0
            hero.buffs.clear()

            // PassiveSkillTrigger.KILLED_OPP
            executer?.let {
                executer.heroBase.skills
                    .filter { isPassiveTriggerable(executer, it, PassiveSkillTrigger.KILLED_OPP) }
                    .forEach { skill ->
                        if (hero.status == HeroStatus.DEAD) {
                            executePassiveSkill(battle, executer, hero, skill)
                        }
                    }
            }

            // PassiveSkillTrigger.ANY_OPP_DIED
            battle.allOtherHeroesAlive(hero).forEach { oppHero ->
                oppHero.heroBase.skills
                    .filter { isPassiveTriggerable(oppHero, it, PassiveSkillTrigger.ANY_OPP_DIED) }
                    .forEach { skill ->
                        if (hero.status == HeroStatus.DEAD) {
                            executePassiveSkill(battle, oppHero, hero, skill)
                        }
                    }
            }

            // PassiveSkillTrigger.SELF_DIED
            hero.heroBase.skills
                .filter { isPassiveTriggerable(hero, it, PassiveSkillTrigger.SELF_DIED) }
                .forEach { skill ->
                    if (hero.status == HeroStatus.DEAD) {
                        executePassiveSkill(battle, hero, hero, skill)
                    }
                }

            // PassiveSkillTrigger.ALLY_DIED
            battle.allAlliedHeroesAlive(hero).forEach { alliedHero ->
                alliedHero.heroBase.skills
                    .filter { isPassiveTriggerable(alliedHero, it, PassiveSkillTrigger.ALLY_DIED) }
                    .forEach { skill ->
                        if (hero.status == HeroStatus.DEAD) {
                            executePassiveSkill(battle, alliedHero, hero, skill)
                        }
                    }
            }
        } else {
            // PassiveSkillTrigger.OWN_HEALTH_UNDER
            hero.heroBase.skills
                .filter { isPassiveTriggerable(hero, it, PassiveSkillTrigger.OWN_HEALTH_UNDER) }
                .forEach { skill ->
                    if ((100 * hero.currentHp) / hero.heroHp <= skill.passiveSkillTriggerValue ?: 0) {
                        executePassiveSkill(battle, hero, hero, skill)
                    }
                }

            // PassiveSkillTrigger.ALLY_HEALTH_UNDER
            battle.allAlliedHeroesAlive(hero).forEach { alliedHero ->
                alliedHero.heroBase.skills
                    .filter { isPassiveTriggerable(alliedHero, it, PassiveSkillTrigger.ALLY_HEALTH_UNDER) }
                    .forEach { skill ->
                        if ((100 * hero.currentHp) / hero.heroHp <= skill.passiveSkillTriggerValue ?: 0) {
                            executePassiveSkill(battle, alliedHero, hero, skill)
                        }
                    }
            }
        }
    }

    private fun applyBuff(battle: Battle, hero: BattleHero, action: HeroSkillAction, target: BattleHero): BattleStepAction {
        val buff = action.effect.buff!!
        var intensity = action.effectValue
        var duration = action.effectDuration!!
        if (action.type == SkillActionType.BUFF) {
            intensity += hero.buffIntensityIncBonus
            duration += hero.buffDurationIncBonus
        }
        if (action.type == SkillActionType.DEBUFF) {
            intensity += hero.debuffIntensityIncBonus
            duration += hero.debuffDurationIncBonus
        }

        var resisted = duration <= 0
        if (!resisted && buff.type == BuffType.DEBUFF) {
            resisted = !procs(100 + hero.getTotalDexterity() - target.getTotalResistance())
        }


        if (!resisted) {
            target.buffs.add(BattleHeroBuff(
                    buff = buff,
                    intensity = intensity,
                    duration = duration,
                    resistance = propertyService.getProperties(buff.propertyType, intensity).filter { it.stat == HeroStat.BUFF_RESISTANCE }.sumBy { it.value1 },
                    sourceHeroId = hero.id
            ))
            target.resetBonus(battle, propertyService)

            if (buff.type == BuffType.DEBUFF) {
                // PassiveSkillTrigger.SELF_DEBUFF
                target.heroBase.skills
                    .filter { isPassiveTriggerable(target, it, PassiveSkillTrigger.SELF_DEBUFF) }
                    .forEach { skill ->executePassiveSkill(battle, target, target, skill) }

                // PassiveSkillTrigger.ALLY_DEBUFF
                battle.allAlliedHeroesAlive(target).forEach { ally ->
                    ally.heroBase.skills
                        .filter { isPassiveTriggerable(ally, it, PassiveSkillTrigger.ALLY_DEBUFF) }
                        .forEach { skill ->executePassiveSkill(battle, ally, target, skill) }
                }
            }

            if (buff.type == BuffType.BUFF) {
                // PassiveSkillTrigger.OPP_BUFF
                battle.allOtherHeroesAlive(target).forEach { opp ->
                    opp.heroBase.skills
                        .filter { isPassiveTriggerable(opp, it, PassiveSkillTrigger.OPP_BUFF) }
                        .forEach { skill -> executePassiveSkill(battle, opp, target, skill) }
                }
            }

            return BattleStepAction(
                    heroPosition = target.position,
                    heroName = target.heroBase.name,
                    type = BattleStepActionType.BUFF,
                    buff = buff,
                    buffIntensity = intensity,
                    buffDuration = duration
            )
        }

        return BattleStepAction(
                heroPosition = target.position,
                heroName = target.heroBase.name,
                type = BattleStepActionType.BLOCKED,
                buff = buff
        )
    }

    private fun applySpeedbarAction(step: BattleStep, hero: BattleHero, action: HeroSkillAction) {
        when (action.effect) {
            PERCENTAGE -> (hero.currentSpeedBar * action.effectValue) / 100
            PERCENTAGE_MAX -> (SPEEDBAR_MAX * action.effectValue) / 100
            else -> 0
        }.takeIf { it > 0 }?.let {
            hero.currentSpeedBar += it
            step.addAction(BattleStepAction(
                heroPosition = hero.position,
                heroName = hero.heroBase.name,
                type = BattleStepActionType.SPEEDBAR,
                speedbarFill = it
            ))
        }

    }

    private fun applySpecialAction(battle: Battle, step: BattleStep, hero: BattleHero, action: HeroSkillAction, target: BattleHero): Boolean {
        var actionDidSomething = false
        when (action.effect) {
            REMOVE_BUFF -> {
                val buff = target.buffs.filter { it.buff.type == BuffType.BUFF }.takeIf { it.isNotEmpty() }?.random()
                if (buff != null) {
                    actionDidSomething = true
                    val resisted = action.effectValue == 0 && !procs(100 + hero.getTotalDexterity() - target.getTotalResistance() - buff.resistance)
                    if (!resisted) {
                        target.buffs.remove(buff)
                    }
                    step.addAction(BattleStepAction(
                            heroPosition = target.position,
                            heroName = target.heroBase.name,
                            type = if (resisted) BattleStepActionType.BLOCKED else BattleStepActionType.BUFF_CLEANED,
                            buff = buff.buff
                    ))
                }
            }
            REMOVE_ALL_BUFFS -> {
                val buffsRemoved = mutableListOf<BattleHeroBuff>()
                target.buffs.filter { it.buff.type == BuffType.BUFF }.forEach { buff ->
                    actionDidSomething = true
                    val resisted = action.effectValue == 0 && !procs(100 + hero.getTotalDexterity() - target.getTotalResistance() - buff.resistance)
                    if (!resisted) {
                        buffsRemoved.add(buff)
                    }
                    step.addAction(BattleStepAction(
                            heroPosition = target.position,
                            heroName = target.heroBase.name,
                            type = if (resisted) BattleStepActionType.BLOCKED else BattleStepActionType.BUFF_CLEANED,
                            buff = buff.buff
                    ))
                }
                target.buffs.removeAll(buffsRemoved)
            }
            REMOVE_DEBUFF -> {
                val debuff = target.buffs.filter { it.buff.type == BuffType.DEBUFF }.takeIf { it.isNotEmpty() }?.random()
                if (debuff != null) {
                    actionDidSomething = true
                    val resisted = action.effectValue == 0 && !procs(100 + hero.getTotalDexterity() - debuff.resistance)
                    if (!resisted) {
                        target.buffs.remove(debuff)
                    }
                    step.addAction(BattleStepAction(
                            heroPosition = target.position,
                            heroName = target.heroBase.name,
                            type = if (resisted) BattleStepActionType.BLOCKED else BattleStepActionType.BUFF_CLEANED,
                            buff = debuff.buff
                    ))
                }
            }
            REMOVE_ALL_DEBUFFS -> {
                val debuffsRemoved = mutableListOf<BattleHeroBuff>()
                target.buffs.filter { it.buff.type == BuffType.DEBUFF }.forEach { debuff ->
                    actionDidSomething = true
                    val resisted = action.effectValue == 0 && !procs(100 + hero.getTotalDexterity() - debuff.resistance)
                    if (!resisted) {
                        debuffsRemoved.add(debuff)
                    }
                    step.addAction(BattleStepAction(
                            heroPosition = target.position,
                            heroName = target.heroBase.name,
                            type = if (resisted) BattleStepActionType.BLOCKED else BattleStepActionType.BUFF_CLEANED,
                            buff = debuff.buff
                    ))
                }
                target.buffs.removeAll(debuffsRemoved)
            }
            RESURRECT -> {
                actionDidSomething = true
                target.status = HeroStatus.ALIVE
                target.currentHp = (action.effectValue * target.heroHp) / 100
                step.addAction(BattleStepAction(
                        heroPosition = target.position,
                        heroName = target.heroBase.name,
                        type = BattleStepActionType.RESURRECTED,
                        healthDiff = target.currentHp
                ))
            }
            SMALL_SHIELD, MEDIUM_SHIELD, LARGE_SHIELD -> {
                actionDidSomething = true
                val value = when (action.effect) {
                    SMALL_SHIELD -> (target.heroHp * 0.25).toInt()
                    MEDIUM_SHIELD -> (target.heroHp * 0.50).toInt()
                    LARGE_SHIELD -> target.heroHp
                    else -> {
                        throw ConfigurationException("Reached unreachable code")
                    }
                }
                val intensity = action.effectValue + hero.buffIntensityIncBonus
                val duration = action.effectDuration!! + hero.buffDurationIncBonus
                target.buffs.add(BattleHeroBuff(
                        buff = Buff.SHIELD,
                        intensity = intensity,
                        duration = duration,
                        value = value,
                        resistance = propertyService.getProperties(PropertyType.SHIELD_BUFF, intensity).filter { it.stat == HeroStat.BUFF_RESISTANCE }.sumBy { it.value1 },
                        sourceHeroId = hero.id
                ))
                step.addAction(BattleStepAction(
                    heroPosition = target.position,
                    heroName = target.heroBase.name,
                    type = BattleStepActionType.BUFF,
                    buff = Buff.SHIELD,
                    buffDuration = duration,
                    buffIntensity = intensity
                ))
            }
            else -> {}
        }
        return actionDidSomething
    }

    private fun applyHealingAction(battle: Battle, hero: BattleHero, action: HeroSkillAction, target: BattleHero): BattleStepAction {
        var healing = when (action.effect) {
            TARGET_MAX_HP -> (target.heroHp * action.effectValue) / 100
            OWN_MAX_HP -> (hero.heroHp * action.effectValue) / 100
            else -> 0
        }
        val maxHealing = max(target.heroHp - target.currentHp, 0)

        healing += (target.getTotalHealingInc() * healing) / 100

        if (battle.heroBelongsToPlayer(target)) {
            battle.fight?.environment?.playerHealingDec?.takeIf { it > 0 }?.let { decrease ->
                healing -= (healing * decrease) / 100
            }
        }

        healing = min(healing, maxHealing)
        healing = max(healing, 0)

        target.currentHp += healing

        return BattleStepAction(
                heroPosition = target.position,
                heroName = target.heroBase.name,
                type = BattleStepActionType.HEALING,
                healthDiff = healing)
    }

    private fun areOpponents(hero1: BattleHero, hero2: BattleHero): Boolean {
        return if (hero_positions.contains(hero1.position)) {
            opp_positions.contains(hero2.position)
        } else {
            hero_positions.contains(hero2.position)
        }
    }

    fun executeStartBattlePassives(battle: Battle) {
        battle.allHeroesAlive().forEach { hero ->
            hero.heroBase.skills
                .filter { isPassiveTriggerable(hero, it, PassiveSkillTrigger.START_STAGE_BUFFING) }
                .forEach { skill ->
                    executePassiveSkill(battle, hero, hero, skill)
                }
        }
        battle.allHeroesAlive().forEach { hero ->
            hero.heroBase.skills
                .filter { isPassiveTriggerable(hero, it, PassiveSkillTrigger.START_STAGE_DEBUFFING) }
                .forEach { skill ->
                    executePassiveSkill(battle, hero, hero, skill)
                }
        }
        battle.allHeroesAlive().forEach { hero ->
            hero.heroBase.skills
                .filter { isPassiveTriggerable(hero, it, PassiveSkillTrigger.START_STAGE_DAMAGING) }
                .forEach { skill ->
                    executePassiveSkill(battle, hero, hero, skill)
                }
        }
    }

    private fun isPassiveTriggerable(hero: BattleHero, skill: HeroSkill, trigger: PassiveSkillTrigger): Boolean {
        return skill.passive &&
                skill.passiveSkillTrigger == trigger &&
                hero.getSkillLevel(skill.number) > 0 &&
                hero.getCooldown(skill.number) <= 0
    }

    private fun executePassiveSkill(battle: Battle, actingHero: BattleHero, target: BattleHero, skill: HeroSkill) {
        val step = BattleStep(
            battleId = battle.id,
            turn = battle.turnsDone,
            phase = BattleStepPhase.PASSIVE,
            actingHero = actingHero.position,
            actingHeroName = actingHero.heroBase.name,
            usedSkill = skill.number,
            usedSkillName = skill.name,
            target = target.position,
            targetName = target.heroBase.name,
            heroStates = battle.getBattleStepHeroStates()
        )
        actingHero.skillUsed(skill.number)
        if (executeSkillActions(battle, step, actingHero, skill, target)) {
            battle.addStep(step)
        } else {
            actingHero.resetSkillCooldown(skill.number)
        }
    }
}
