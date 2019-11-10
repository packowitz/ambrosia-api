package io.pacworx.ambrosia.io.pacworx.ambrosia.battle

import io.pacworx.ambrosia.io.pacworx.ambrosia.enums.*
import io.pacworx.ambrosia.io.pacworx.ambrosia.enums.SkillActionEffect.*
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
        private val hero_positions = listOf(HeroPosition.HERO1, HeroPosition.HERO2, HeroPosition.HERO3, HeroPosition.HERO4)
        private val opp_positions = listOf(HeroPosition.OPP1, HeroPosition.OPP2, HeroPosition.OPP3, HeroPosition.OPP4)
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
        battle.applyBonuses(propertyService)
        val step = BattleStep(
                turn = battle.turnsDone,
                phase = BattleStepPhase.MAIN,
                actingHero = hero.position,
                usedSkill = skill.number,
                target = target.position,
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

        hero.currentSpeedBar -= SPEEDBAR_MAX
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

    private fun isTargetEligible(battle: Battle, hero: BattleHero, skill: HeroSkill, target: BattleHero): Boolean {
        val isPlayer = battle.heroBelongsToPlayer(hero)
        return when (skill.target) {
            SkillTarget.OPPONENT -> isPlayer == battle.heroBelongsToOpponent(target) && (target.isTaunting() || battle.allAlliedHeroesAlive(target).none { it.isTaunting() })
            SkillTarget.SELF -> hero.position == target.position
            SkillTarget.ALL_OWN -> isPlayer == battle.heroBelongsToPlayer(target)
            SkillTarget.OPP_IGNORE_TAUNT -> isPlayer == battle.heroBelongsToOpponent(target)
            SkillTarget.DEAD -> isPlayer == battle.heroBelongsToPlayer(target) && target.status == HeroStatus.DEAD
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
                type = BattleStepActionType.DODGED
            ))
            return
        }

        if (!isFreeAttack && areOpponents(hero, damageDealer)) {
            hero.willCounter = hero.willCounter || procs(hero.getTotalCounterChance())
        }

        val crit = Random.nextInt(100) < hero.getTotalCrit()
        val superCrit = crit && Random.nextInt(100) < hero.heroSuperCritChance + hero.superCritChanceBonus

        var damage = (baseDamage * action.effectValue) / 100
        if (superCrit) {
            damage += ((damage * (hero.getTotalCritMult() + 150)) / 100)
        } else if (crit) {
            damage += ((damage * hero.getTotalCritMult()) / 100)
        }

        val reflectDamage = hero.getTotalReflect().takeIf { it > 0 }?.let { damage * it / 100 } ?: 0
        damage -= reflectDamage

        damage *= damageDealer.getTotalRedDamageInc().takeIf { hero.color == Color.RED && it != 0 }?.let { (100 + it) / 100 } ?: 1
        damage *= damageDealer.getTotalGreenDamageInc().takeIf { hero.color == Color.GREEN && it != 0 }?.let { (100 + it) / 100 } ?: 1
        damage *= damageDealer.getTotalBlueDamageInc().takeIf { hero.color == Color.BLUE && it != 0 }?.let { (100 + it) / 100 } ?: 1

        val armorPiercedDamage = damageDealer.getTotalArmorPiercing().takeIf { it > 0 }?.let { (damage * it) / 100 } ?: 0

        val targetArmor = hero.getTotalArmor()
        val targetHealth = hero.currentHp
        val dmgArmorRatio: Int = 100 * (damage - armorPiercedDamage) / targetArmor
        val property = battleProps.find { dmgArmorRatio <= it.level!! } ?: battleProps.last()

        var armorLoss = (hero.currentArmor * property.value1) / 100
        armorLoss *= (100 + damageDealer.getTotalArmorExtraDamage()) / 100
        var healthLoss = armorPiercedDamage + ((damage - armorPiercedDamage) * property.value2!!) / 100
        healthLoss *= (100 + damageDealer.getTotalHealthExtraDamage()) / 100

        receiveDamage(battle, hero, armorLoss, healthLoss, damageDealer)

        val lifeSteal = damageDealer.getTotalLifesteal().takeIf { it > 0 }?.let { (healthLoss * it) / 100 } ?: 0

        step.addAction(BattleStepAction(
                heroPosition = hero.position,
                type = BattleStepActionType.DAMAGE,
                crit = crit,
                superCrit = superCrit,
                baseDamage = damage,
                targetArmor = targetArmor,
                targetHealth = targetHealth,
                armorDiff = -armorLoss,
                healthDiff = -healthLoss
        ))

        if (hero.status == HeroStatus.DEAD) {
            step.addAction(BattleStepAction(heroPosition = hero.position, type = BattleStepActionType.DEAD))
        }

        if (lifeSteal > 0 && damageDealer.currentHp < damageDealer.heroHp) {
            var healing = lifeSteal * (100 + damageDealer.getTotalHealingInc()) / 100
            healing = min(healing, damageDealer.heroHp - damageDealer.currentHp)
            damageDealer.currentHp += healing
            step.addAction(BattleStepAction(heroPosition = damageDealer.position, type = BattleStepActionType.HEALING, healthDiff = healing))
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
                usedSkill = skill.number,
                target = hero.position,
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
                    else -> {}
                }
            }
        }
    }

    private fun applyReflectDamage(battle: Battle, hero: BattleHero, step: BattleStep, reflectDamage: Int) {
        val targetArmor = hero.getTotalArmor()
        val targetHealth = hero.currentHp
        val dmgArmorRatio: Int = 100 * reflectDamage / targetArmor
        val property = battleProps.find { dmgArmorRatio <= it.level!! } ?: battleProps.last()

        val armorLoss = (hero.currentArmor * property.value1) / 100
        val healthLoss = (reflectDamage * property.value2!!) / 100

        receiveDamage(battle, hero, armorLoss, healthLoss)

        step.addAction(BattleStepAction(
            heroPosition = hero.position,
            type = BattleStepActionType.DAMAGE,
            crit = false,
            superCrit = false,
            baseDamage = reflectDamage,
            targetArmor = targetArmor,
            targetHealth = targetHealth,
            armorDiff = -armorLoss,
            healthDiff = -healthLoss
        ))

        if (hero.status == HeroStatus.DEAD) {
            step.addAction(BattleStepAction(heroPosition = hero.position, type = BattleStepActionType.DEAD))
        }
    }

    fun receiveDamage(battle: Battle, hero: BattleHero, armorLoss: Int, healthLoss: Int, executer: BattleHero? = null) {
        hero.currentArmor -= armorLoss
        hero.currentHp -= healthLoss
        if (hero.currentHp <= 0) {
            hero.status = HeroStatus.DEAD
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
                                usedSkill = skill.number,
                                target = hero.position,
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
                                usedSkill = skill.number,
                                target = hero.position,
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
                            usedSkill = skill.number,
                            target = hero.position,
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
                                usedSkill = skill.number,
                                target = hero.position,
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
                            usedSkill = skill.number,
                            target = hero.position,
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
                                usedSkill = skill.number,
                                target = hero.position,
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
        var intesity = action.effectValue
        var duration = action.effectDuration!!
        if (action.type == SkillActionType.BUFF) {
            intesity += hero.buffIntensityIncBonus
            duration += hero.buffDurationIncBonus
        }
        if (action.type == SkillActionType.DEBUFF) {
            intesity += hero.debuffIntensityIncBonus
            duration += hero.debuffDurationIncBonus
        }

        var resisted = duration <= 0
        if (!resisted && buff.type == BuffType.DEBUFF) {
            resisted = !procs(100 + hero.getTotalDexterity() - target.getTotalResistance())
        }

        if (!resisted) {
            target.buffs.add(BattleHeroBuff(
                    buff = buff,
                    intensity = intesity,
                    duration = duration,
                    sourceHeroId = hero.id
            ))
            target.resetBonus(battle, propertyService)

            return BattleStepAction(
                    heroPosition = target.position,
                    type = BattleStepActionType.BUFF,
                    buff = buff,
                    buffIntensity = intesity,
                    buffDuration = duration
            )
        }

        return BattleStepAction(
                heroPosition = target.position,
                type = BattleStepActionType.BLOCKED,
                buff = buff
        )
    }

    private fun applySpeedbarAction(hero: BattleHero, action: HeroSkillAction) {
        when (action.effect) {
            PERCENTAGE -> hero.currentSpeedBar += (SPEEDBAR_MAX * action.effectValue) / 100
            else -> {}
        }
    }

    private fun applySpecialAction(battle: Battle, step: BattleStep, hero: BattleHero, action: HeroSkillAction, target: BattleHero) {
        when (action.effect) {
            REMOVE_BUFF -> {
                val buff = target.buffs.takeIf { it.isNotEmpty() }?.random()
                if (buff != null) {
                    val resisted = action.effectValue == 0 && !procs(100 + hero.getTotalDexterity() - target.getTotalResistance())
                    if (!resisted) {
                        target.buffs.remove(buff)
                    }
                    step.addAction(BattleStepAction(
                        heroPosition = target.position,
                        type = if (resisted) BattleStepActionType.BLOCKED else BattleStepActionType.BUFF_CLEANED,
                        buff = buff.buff
                    ))
                }
            }
            REMOVE_ALL_BUFFS -> {
                val buffsRemoved = mutableListOf<BattleHeroBuff>()
                target.buffs.forEach { buff ->
                    val resisted = action.effectValue == 0 && !procs(100 + hero.getTotalDexterity() - target.getTotalResistance())
                    if (!resisted) {
                        buffsRemoved.add(buff)
                    }
                    step.addAction(BattleStepAction(
                        heroPosition = target.position,
                        type = if (resisted) BattleStepActionType.BLOCKED else BattleStepActionType.BUFF_CLEANED,
                        buff = buff.buff
                    ))
                }
                target.buffs.removeAll(buffsRemoved)
            }
            else -> {}
        }
    }

    private fun applyHealingAction(hero: BattleHero, action: HeroSkillAction, target: BattleHero): BattleStepAction {
        var healing = when (action.effect) {
            SkillActionEffect.TARGET_MAX_HP -> (target.heroHp * action.effectValue) / 100
            SkillActionEffect.OWN_MAX_HP -> (hero.heroHp * action.effectValue) / 100
            else -> 0
        }
        healing += (hero.getTotalHealingInc() * healing) / 100
        healing = min(healing, target.heroHp - target.currentHp)

        hero.currentHp += healing

        return BattleStepAction(heroPosition = target.position, type = BattleStepActionType.HEALING, healthDiff = healing)
    }

    private fun areOpponents(hero1: BattleHero, hero2: BattleHero): Boolean {
        return if (hero_positions.contains(hero1.position)) {
            opp_positions.contains(hero2.position)
        } else {
            hero_positions.contains(hero2.position)
        }
    }
}
