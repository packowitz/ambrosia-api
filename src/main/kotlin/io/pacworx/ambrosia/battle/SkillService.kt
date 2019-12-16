package io.pacworx.ambrosia.io.pacworx.ambrosia.battle

import io.pacworx.ambrosia.io.pacworx.ambrosia.battle.BattleService.Companion.SPEEDBAR_MAX
import io.pacworx.ambrosia.io.pacworx.ambrosia.enums.*
import io.pacworx.ambrosia.io.pacworx.ambrosia.enums.SkillActionEffect.*
import io.pacworx.ambrosia.io.pacworx.ambrosia.models.DynamicProperty
import io.pacworx.ambrosia.io.pacworx.ambrosia.models.HeroSkill
import io.pacworx.ambrosia.io.pacworx.ambrosia.models.HeroSkillAction
import io.pacworx.ambrosia.io.pacworx.ambrosia.services.PropertyService
import org.springframework.stereotype.Service
import java.lang.RuntimeException
import kotlin.math.max
import kotlin.math.min
import kotlin.random.Random

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
        battle.steps.add(step)

        executeSkillActions(battle, step, hero, skill, target)

        // check for counter attacks
        battle.allOtherHeroesAlive(hero).filter { it.willCounter }.forEach { counterHero ->
            if (hero.status != HeroStatus.DEAD) {
                doCounter(battle, counterHero, hero, BattleStepPhase.Z_COUNTER)
            }
        }

        hero.skillUsed(skill.number)
        hero.afterTurn(battle, propertyService)
        battle.checkStatus()
    }

    private fun executeSkillActions(battle: Battle, step: BattleStep, hero: BattleHero, skill: HeroSkill, target: BattleHero, excludedActionTypes: List<SkillActionType> = listOf()) {
        var damage = 0
        var lastActionProced: Boolean? = null
        skill.actions.forEach { action ->
            if (!actionTriggers(hero, action, step, lastActionProced)) {
                lastActionProced = null
                return@forEach
            }
            if (!procs(action.triggerChance)) {
                lastActionProced = false
            }
            lastActionProced = true
            when (action.type) {
                SkillActionType.DAMAGE ->
                    if (!excludedActionTypes.contains(SkillActionType.DAMAGE)) {
                        damage += handleDamageAction(hero, action, damage)
                    }
                SkillActionType.DEAL_DAMAGE ->
                    if (!excludedActionTypes.contains(SkillActionType.DEAL_DAMAGE)) {
                        findTargets(battle, hero, action, target)
                                .forEach {
                                    dealDamage(battle, it, hero, action, damage, step)
                                }
                    }
                SkillActionType.BUFF, SkillActionType.DEBUFF ->
                    if (!excludedActionTypes.contains(SkillActionType.DEBUFF)) {
                        findTargets(battle, hero, action, target)
                                .forEach {
                                    step.addAction(applyBuff(battle, hero, action, it))
                                }
                    }
                SkillActionType.SPEEDBAR ->
                    if (!excludedActionTypes.contains(SkillActionType.SPEEDBAR)) {
                        findTargets(battle, hero, action, target)
                                .forEach {
                                    applySpeedbarAction(it, action)
                                }
                    }
                SkillActionType.HEAL ->
                    if (!excludedActionTypes.contains(SkillActionType.HEAL)) {
                        findTargets(battle, hero, action, target)
                                .forEach {
                                    step.addAction(applyHealingAction(hero, action, it))
                                }
                    }
                SkillActionType.PASSIVE_STAT ->
                    if (!excludedActionTypes.contains(SkillActionType.PASSIVE_STAT)) {
                        findTargets(battle, hero, action, target)
                                .forEach {
                                    action.effect.stat?.apply(it, action.effectValue)
                                }
                    }
                SkillActionType.SPECIAL ->
                    if (!excludedActionTypes.contains(SkillActionType.SPECIAL)) {
                        findTargets(battle, hero, action, target)
                                .forEach {
                                    applySpecialAction(battle, step, hero, action, it)
                                }
                    }
            }
        }
    }

    private fun actionTriggers(hero: BattleHero, action: HeroSkillAction, step: BattleStep, lastActionProced: Boolean?): Boolean {
        return when (action.trigger) {
            SkillActionTrigger.ALWAYS -> true
            SkillActionTrigger.S1_LVL -> action.triggerValue!!.contains(hero.skill1Lvl.toString())
            SkillActionTrigger.S2_LVL -> hero.skill2Lvl?.let { action.triggerValue!!.contains(it.toString()) } ?: false
            SkillActionTrigger.S3_LVL -> hero.skill3Lvl?.let { action.triggerValue!!.contains(it.toString()) } ?: false
            SkillActionTrigger.S4_LVL -> hero.skill4Lvl?.let { action.triggerValue!!.contains(it.toString()) } ?: false
            SkillActionTrigger.S5_LVL -> hero.skill5Lvl?.let { action.triggerValue!!.contains(it.toString()) } ?: false
            SkillActionTrigger.S6_LVL -> hero.skill6Lvl?.let { action.triggerValue!!.contains(it.toString()) } ?: false
            SkillActionTrigger.S7_LVL -> hero.skill7Lvl?.let { action.triggerValue!!.contains(it.toString()) } ?: false
            SkillActionTrigger.PREV_ACTION_PROCED -> lastActionProced == true
            SkillActionTrigger.PREV_ACTION_NOT_PROCED -> lastActionProced == false
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
        return when (action.target) {
            SkillActionTarget.TARGET -> listOf(target)
            SkillActionTarget.ALL_OPP -> battle.allOtherHeroesAlive(hero)
            SkillActionTarget.ALL_OTHER_OPP -> battle.allOtherHeroesAlive(hero).filter { it.position != target.position }
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
            SkillActionTarget.ALL_ALLIES -> battle.allAlliedHeroesAlive(hero)
            SkillActionTarget.ALL_OTHER_ALLIES ->
                battle.allAlliedHeroesAlive(hero).filter { it.position != hero.position }
            SkillActionTarget.RANDOM_ALLY -> listOfNotNull(battle.allAlliedHeroesAlive(hero).random())
            SkillActionTarget.RANDOM_OTHER_ALLY ->
                listOfNotNull(battle.allAlliedHeroesAlive(hero).filter { it.position != hero.position }.takeIf { it.isNotEmpty() }?.random())
        }
    }

    private fun handleDamageAction(hero: BattleHero, action: HeroSkillAction, damage: Int): Int {
        return when (action.effect) {
            STRENGTH -> (hero.getTotalStrength() * action.effectValue) / 100
            ARMOR -> (hero.getTotalArmor() * action.effectValue) / 100
            ARMOR_MAX -> (hero.getTotalMaxArmor() * action.effectValue) / 100
            HP -> (hero.currentHp * action.effectValue) / 100
            HP_MAX -> (hero.heroHp * action.effectValue) / 100
            DEXTERITY -> (hero.getTotalDexterity() * action.effectValue) / 100
            RESISTANCE -> (hero.getTotalResistance() * action.effectValue) / 100
            MULTIPLIER -> (damage * action.effectValue) / 100
            else -> 0
        }
    }

    private fun dealDamage(battle: Battle, hero: BattleHero, damageDealer: BattleHero, action: HeroSkillAction, baseDamage: Int, step: BattleStep, isFreeAttack: Boolean = false) {
        if (procs(hero.getTotalDodgeChance())) {
            step.addAction(BattleStepAction(
                    heroPosition = hero.position,
                    heroName = hero.heroBase.name,
                    type = BattleStepActionType.DODGED
            ))
            return
        }

        if (!isFreeAttack && areOpponents(hero, damageDealer)) {
            hero.willCounter = hero.willCounter || procs(hero.getTotalCounterChance())
        }

        val crit = Random.nextInt(100) < hero.getTotalCrit()
        val superCrit = crit && Random.nextInt(100) < hero.heroSuperCritChance + hero.superCritChanceBonus

        var baseDamage = (baseDamage * action.effectValue) / 100
        if (superCrit) {
            baseDamage += ((baseDamage * (hero.getTotalCritMult() + 150)) / 100)
        } else if (crit) {
            baseDamage += ((baseDamage * hero.getTotalCritMult()) / 100)
        }

        val reflectDamage = hero.getTotalReflect().takeIf { it > 0 }?.let { baseDamage * it / 100 } ?: 0
        baseDamage -= reflectDamage

        baseDamage *= damageDealer.getTotalRedDamageInc().takeIf { hero.color == Color.RED && it != 0 }?.let { (100 + it) / 100 }
                ?: 1
        baseDamage *= damageDealer.getTotalGreenDamageInc().takeIf { hero.color == Color.GREEN && it != 0 }?.let { (100 + it) / 100 }
                ?: 1
        baseDamage *= damageDealer.getTotalBlueDamageInc().takeIf { hero.color == Color.BLUE && it != 0 }?.let { (100 + it) / 100 }
                ?: 1

        val damage = shieldCalculation(hero, baseDamage)

        val armorPiercedDamage = damageDealer.getTotalArmorPiercing().takeIf { it > 0 }?.let { (damage * it) / 100 }
                ?: 0

        val targetArmor = hero.getTotalArmor()
        val targetHealth = hero.currentHp
        val property = if (targetArmor > 0) {
            val dmgArmorRatio: Int = 100 * (damage - armorPiercedDamage) / targetArmor
            battleProps.find { dmgArmorRatio <= it.level!! } ?: battleProps.last()
        } else {
            battleProps.last()
        }

        var armorLoss = (hero.currentArmor * property.value1) / 100
        armorLoss *= (100 + damageDealer.getTotalArmorExtraDamage()) / 100
        armorLoss = min(armorLoss, targetArmor)
        var healthLoss = armorPiercedDamage + ((damage - armorPiercedDamage) * property.value2!!) / 100
        healthLoss *= (100 + damageDealer.getTotalHealthExtraDamage()) / 100

        receiveDamage(battle, hero, armorLoss, healthLoss, damageDealer)

        val lifeSteal = damageDealer.getTotalLifesteal().takeIf { it > 0 }?.let { (healthLoss * it) / 100 } ?: 0

        step.addAction(BattleStepAction(
                heroPosition = hero.position,
                heroName = hero.heroBase.name,
                type = BattleStepActionType.DAMAGE,
                crit = crit,
                superCrit = superCrit,
                baseDamage = baseDamage,
                shieldAbsorb = baseDamage - damage,
                targetArmor = targetArmor,
                targetHealth = targetHealth,
                armorDiff = -armorLoss,
                healthDiff = -healthLoss
        ))

        if (hero.status == HeroStatus.DEAD) {
            step.addAction(BattleStepAction(
                    heroPosition = hero.position,
                    heroName = hero.heroBase.name,
                    type = BattleStepActionType.DEAD))
        }

        if (lifeSteal > 0 && damageDealer.currentHp < damageDealer.heroHp) {
            val maxHealing = max(damageDealer.heroHp - damageDealer.currentHp, 0)
            var healing = lifeSteal * (100 + damageDealer.getTotalHealingInc()) / 100
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
        counterHero.heroBase.skills.find { it.number == 1 }?.let { skill ->
            val step = BattleStep(
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
            battle.steps.add(step)
            skill.actions.forEach { action ->
                when (action.type) {
                    SkillActionType.DAMAGE -> damage += handleDamageAction(counterHero, action, damage)
                    SkillActionType.DEAL_DAMAGE ->
                        findTargets(battle, counterHero, action, hero)
                                .forEach {
                                    dealDamage(battle, it, counterHero, action, damage, step, true)
                                }
                    SkillActionType.HEAL ->
                        findTargets(battle, counterHero, action, hero)
                                .forEach {
                                    step.addAction(applyHealingAction(counterHero, action, it))
                                }
                    SkillActionType.PASSIVE_STAT ->
                        findTargets(battle, counterHero, action, hero)
                                .forEach {
                                    action.effect.stat?.apply(it, action.effectValue)
                                }
                    SkillActionType.SPECIAL ->
                        findTargets(battle, counterHero, action, hero)
                                .forEach {
                                    applySpecialAction(battle, step, hero, action, it)
                                }
                    else -> {
                    }
                }
            }
        }
    }

    private fun applyReflectDamage(battle: Battle, hero: BattleHero, step: BattleStep, reflectDamage: Int) {
        val targetArmor = hero.getTotalArmor()
        val targetHealth = hero.currentHp
        val damage = shieldCalculation(hero, reflectDamage)
        val dmgArmorRatio: Int = 100 * damage / targetArmor
        val property = battleProps.find { dmgArmorRatio <= it.level!! } ?: battleProps.last()

        val armorLoss = (hero.currentArmor * property.value1) / 100
        val healthLoss = (damage * property.value2!!) / 100

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
                        .filter { it.passive && it.passiveSkillTrigger == PassiveSkillTrigger.KILLED_OPP && executer.getCooldown(it.number) <= 0 }
                        .forEach { skill ->
                            if (hero.status == HeroStatus.DEAD) {
                                val step = BattleStep(
                                        turn = battle.turnsDone,
                                        phase = BattleStepPhase.PASSIVE,
                                        actingHero = executer.position,
                                        actingHeroName = executer.heroBase.name,
                                        usedSkill = skill.number,
                                        usedSkillName = skill.name,
                                        target = hero.position,
                                        targetName = hero.heroBase.name,
                                        heroStates = battle.getBattleStepHeroStates()
                                )
                                battle.steps.add(step)
                                executeSkillActions(battle, step, executer, skill, hero)
                                executer.skillUsed(skill.number)
                            }
                        }
            }

            // PassiveSkillTrigger.ANY_OPP_DIED
            battle.allOtherHeroesAlive(hero).forEach { oppHero ->
                oppHero.heroBase.skills
                        .filter { it.passive && it.passiveSkillTrigger == PassiveSkillTrigger.ANY_OPP_DIED && oppHero.getCooldown(it.number) <= 0 }
                        .forEach { skill ->
                            if (hero.status == HeroStatus.DEAD) {
                                val step = BattleStep(
                                        turn = battle.turnsDone,
                                        phase = BattleStepPhase.PASSIVE,
                                        actingHero = oppHero.position,
                                        actingHeroName = oppHero.heroBase.name,
                                        usedSkill = skill.number,
                                        usedSkillName = skill.name,
                                        target = hero.position,
                                        targetName = hero.heroBase.name,
                                        heroStates = battle.getBattleStepHeroStates()
                                )
                                battle.steps.add(step)
                                executeSkillActions(battle, step, oppHero, skill, hero)
                                oppHero.skillUsed(skill.number)
                            }
                        }
            }

            // PassiveSkillTrigger.SELF_DIED
            hero.heroBase.skills
                    .filter { it.passive && it.passiveSkillTrigger == PassiveSkillTrigger.SELF_DIED && hero.getCooldown(it.number) <= 0 }
                    .forEach { skill ->
                        if (hero.status == HeroStatus.DEAD) {
                            val step = BattleStep(
                                    turn = battle.turnsDone,
                                    phase = BattleStepPhase.PASSIVE,
                                    actingHero = hero.position,
                                    actingHeroName = hero.heroBase.name,
                                    usedSkill = skill.number,
                                    usedSkillName = skill.name,
                                    target = hero.position,
                                    targetName = hero.heroBase.name,
                                    heroStates = battle.getBattleStepHeroStates()
                            )
                            battle.steps.add(step)
                            executeSkillActions(battle, step, hero, skill, hero)
                            hero.skillUsed(skill.number)
                        }
                    }

            // PassiveSkillTrigger.ALLY_DIED
            battle.allAlliedHeroesAlive(hero).forEach { alliedHero ->
                alliedHero.heroBase.skills
                        .filter { it.passive && it.passiveSkillTrigger == PassiveSkillTrigger.ALLY_DIED && alliedHero.getCooldown(it.number) <= 0 }
                        .forEach { skill ->
                            if (hero.status == HeroStatus.DEAD) {
                                val step = BattleStep(
                                        turn = battle.turnsDone,
                                        phase = BattleStepPhase.PASSIVE,
                                        actingHero = alliedHero.position,
                                        actingHeroName = alliedHero.heroBase.name,
                                        usedSkill = skill.number,
                                        usedSkillName = skill.name,
                                        target = hero.position,
                                        targetName = hero.heroBase.name,
                                        heroStates = battle.getBattleStepHeroStates()
                                )
                                battle.steps.add(step)
                                executeSkillActions(battle, step, alliedHero, skill, hero)
                                alliedHero.skillUsed(skill.number)
                            }
                        }
            }
        } else {
            // PassiveSkillTrigger.OWN_HEALTH_UNDER
            hero.heroBase.skills
                    .filter { it.passive && it.passiveSkillTrigger == PassiveSkillTrigger.OWN_HEALTH_UNDER && hero.getCooldown(it.number) <= 0 }
                    .forEach { skill ->
                        if ((100 * hero.currentHp) / hero.heroHp <= skill.passiveSkillTriggerValue ?: 0) {
                            val step = BattleStep(
                                    turn = battle.turnsDone,
                                    phase = BattleStepPhase.PASSIVE,
                                    actingHero = hero.position,
                                    actingHeroName = hero.heroBase.name,
                                    usedSkill = skill.number,
                                    usedSkillName = skill.name,
                                    target = hero.position,
                                    targetName = hero.heroBase.name,
                                    heroStates = battle.getBattleStepHeroStates()
                            )
                            battle.steps.add(step)
                            executeSkillActions(battle, step, hero, skill, hero)
                            hero.skillUsed(skill.number)
                        }
                    }

            // PassiveSkillTrigger.ALLY_HEALTH_UNDER
            battle.allAlliedHeroesAlive(hero).forEach { alliedHero ->
                alliedHero.heroBase.skills
                        .filter { it.passive && it.passiveSkillTrigger == PassiveSkillTrigger.ALLY_HEALTH_UNDER && alliedHero.getCooldown(it.number) <= 0 }
                        .forEach { skill ->
                            if ((100 * hero.currentHp) / hero.heroHp <= skill.passiveSkillTriggerValue ?: 0) {
                                val step = BattleStep(
                                        turn = battle.turnsDone,
                                        phase = BattleStepPhase.PASSIVE,
                                        actingHero = alliedHero.position,
                                        actingHeroName = alliedHero.heroBase.name,
                                        usedSkill = skill.number,
                                        usedSkillName = skill.name,
                                        target = hero.position,
                                        targetName = hero.heroBase.name,
                                        heroStates = battle.getBattleStepHeroStates()
                                )
                                battle.steps.add(step)
                                executeSkillActions(battle, step, alliedHero, skill, hero)
                                alliedHero.skillUsed(skill.number)
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
                        .filter { it.passive && it.passiveSkillTrigger == PassiveSkillTrigger.SELF_DEBUFF && target.getCooldown(it.number) <= 0 }
                        .forEach { skill ->
                            val step = BattleStep(
                                    turn = battle.turnsDone,
                                    phase = BattleStepPhase.PASSIVE,
                                    actingHero = target.position,
                                    actingHeroName = target.heroBase.name,
                                    usedSkill = skill.number,
                                    usedSkillName = skill.name,
                                    target = target.position,
                                    targetName = target.heroBase.name,
                                    heroStates = battle.getBattleStepHeroStates()
                            )
                            battle.steps.add(step)
                            executeSkillActions(battle, step, target, skill, target)
                            target.skillUsed(skill.number)
                        }

                // PassiveSkillTrigger.ALLY_DEBUFF
                battle.allAlliedHeroesAlive(target).forEach { ally ->
                    ally.heroBase.skills
                            .filter { it.passive && it.passiveSkillTrigger == PassiveSkillTrigger.ALLY_DEBUFF && ally.getCooldown(it.number) <= 0 }
                            .forEach { skill ->
                                val step = BattleStep(
                                        turn = battle.turnsDone,
                                        phase = BattleStepPhase.PASSIVE,
                                        actingHero = ally.position,
                                        actingHeroName = ally.heroBase.name,
                                        usedSkill = skill.number,
                                        usedSkillName = skill.name,
                                        target = target.position,
                                        targetName = target.heroBase.name,
                                        heroStates = battle.getBattleStepHeroStates()
                                )
                                battle.steps.add(step)
                                executeSkillActions(battle, step, ally, skill, target)
                                ally.skillUsed(skill.number)
                            }
                }
            }

            if (buff.type == BuffType.BUFF) {
                // PassiveSkillTrigger.OPP_BUFF
                battle.allOtherHeroesAlive(target).forEach { opp ->
                    opp.heroBase.skills
                            .filter { it.passive && it.passiveSkillTrigger == PassiveSkillTrigger.OPP_BUFF && opp.getCooldown(it.number) <= 0 }
                            .forEach { skill ->
                                val step = BattleStep(
                                        turn = battle.turnsDone,
                                        phase = BattleStepPhase.PASSIVE,
                                        actingHero = opp.position,
                                        actingHeroName = opp.heroBase.name,
                                        usedSkill = skill.number,
                                        usedSkillName = skill.name,
                                        target = target.position,
                                        targetName = target.heroBase.name,
                                        heroStates = battle.getBattleStepHeroStates()
                                )
                                battle.steps.add(step)
                                executeSkillActions(battle, step, opp, skill, target)
                                opp.skillUsed(skill.number)
                            }
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

    private fun applySpeedbarAction(hero: BattleHero, action: HeroSkillAction) {
        when (action.effect) {
            PERCENTAGE -> hero.currentSpeedBar += (SPEEDBAR_MAX * action.effectValue) / 100
            else -> {
            }
        }
    }

    private fun applySpecialAction(battle: Battle, step: BattleStep, hero: BattleHero, action: HeroSkillAction, target: BattleHero) {
        when (action.effect) {
            REMOVE_BUFF -> {
                val buff = target.buffs.filter { it.buff.type == BuffType.BUFF }.takeIf { it.isNotEmpty() }?.random()
                if (buff != null) {
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
                val value = when (action.effect) {
                    SMALL_SHIELD -> (target.heroHp * 0.25).toInt()
                    MEDIUM_SHIELD -> (target.heroHp * 0.50).toInt()
                    LARGE_SHIELD -> target.heroHp
                    else -> {
                        throw RuntimeException("Unreachable code")
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
                        buff = Buff.SHIELD
                ))
            }
            else -> {
            }
        }
    }

    private fun applyHealingAction(hero: BattleHero, action: HeroSkillAction, target: BattleHero): BattleStepAction {
        var healing = when (action.effect) {
            SkillActionEffect.TARGET_MAX_HP -> (target.heroHp * action.effectValue) / 100
            SkillActionEffect.OWN_MAX_HP -> (hero.heroHp * action.effectValue) / 100
            else -> 0
        }
        val maxHealing = max(target.heroHp - target.currentHp, 0)
        healing += (target.getTotalHealingInc() * healing) / 100
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
}
