package io.pacworx.ambrosia.io.pacworx.ambrosia.models

import io.pacworx.ambrosia.io.pacworx.ambrosia.enums.*
import java.lang.RuntimeException
import javax.persistence.*

@Entity
data class Gear(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    val playerId: Long,
    var equippedTo: Long? = null,
    @Enumerated(EnumType.STRING)
    val set: GearSet,
    @Enumerated(EnumType.STRING)
    val rarity: Rarity,
    @Enumerated(EnumType.STRING)
    val type: GearType,
    @Enumerated(EnumType.STRING)
    val stat: GearStat,
    val statValue: Int,
    @Enumerated(EnumType.STRING)
    val jewelSlot1: GearJewelSlot? = null,
    @Enumerated(EnumType.STRING)
    var jewel1Type: JewelType? = null,
    var jewel1Level: Int? = null,
    @Enumerated(EnumType.STRING)
    val jewelSlot2: GearJewelSlot? = null,
    @Enumerated(EnumType.STRING)
    var jewel2Type: JewelType? = null,
    var jewel2Level: Int? = null,
    @Enumerated(EnumType.STRING)
    val jewelSlot3: GearJewelSlot? = null,
    @Enumerated(EnumType.STRING)
    var jewel3Type: JewelType? = null,
    var jewel3Level: Int? = null,
    @Enumerated(EnumType.STRING)
    val jewelSlot4: GearJewelSlot? = null,
    @Enumerated(EnumType.STRING)
    var jewel4Type: JewelType? = null,
    var jewel4Level: Int? = null,
    val specialJewelSlot: Boolean = false,
    @Enumerated(EnumType.STRING)
    var specialJewelType: JewelType? = null,
    var specialJewelLevel: Int? = null
) {

    fun apply(hero: Hero) {
        when(stat) {
            GearStat.STRENGTH_ABS -> hero.strengthAbsBonus += statValue
            GearStat.STRENGTH_PERC -> hero.strengthPercBonus += statValue
            GearStat.HP_ABS -> hero.hpAbsBonus += statValue
            GearStat.HP_PERC -> hero.hpPercBonus += statValue
            GearStat.ARMOR_ABS -> hero.armorAbsBonus += statValue
            GearStat.ARMOR_PERC -> hero.armorPercBonus += statValue
            GearStat.SPEED -> hero.initiativeBonus += statValue
            GearStat.CRIT -> hero.critBonus += statValue
            GearStat.CRIT_MULT -> hero.critMultBonus += statValue
            GearStat.DEXTERITY -> hero.dexterityBonus += statValue
            GearStat.RESISTANCE -> hero.resistanceBonus += statValue
        }
        jewel1Type?.apply(hero, jewel1Level!!)
        jewel2Type?.apply(hero, jewel2Level!!)
        jewel3Type?.apply(hero, jewel3Level!!)
        jewel4Type?.apply(hero, jewel4Level!!)
        specialJewelType?.apply(hero, specialJewelLevel!!)
    }

    fun getJewel(slot: Int): Pair<JewelType, Int>? {
        return when(slot) {
            0 -> specialJewelType?.let { Pair(it, specialJewelLevel!!) }
            1 -> jewel1Type?.let { Pair(it, jewel1Level!!) }
            2 -> jewel2Type?.let { Pair(it, jewel2Level!!) }
            3 -> jewel3Type?.let { Pair(it, jewel3Level!!) }
            4 -> jewel4Type?.let { Pair(it, jewel4Level!!) }
            else -> throw RuntimeException("Jewel slot $slot is not supported")
        }
    }

    fun getJewelSlot(slot: Int): GearJewelSlot? {
        return when (slot) {
            0 -> GearJewelSlot.SPECIAL
            1 -> this.jewelSlot1
            2 -> this.jewelSlot2
            3 -> this.jewelSlot3
            4 -> this.jewelSlot4
            else -> null
        }
    }

    fun pluginJewel(slot: Int, jewelType: JewelType, jewelLevel: Int) {
        when (slot) {
            0 -> {
                specialJewelType = jewelType
                specialJewelLevel = jewelLevel
            }
            1 -> {
                jewel1Type = jewelType
                jewel1Level = jewelLevel
            }
            2 -> {
                jewel2Type = jewelType
                jewel2Level = jewelLevel
            }
            3 -> {
                jewel3Type = jewelType
                jewel3Level = jewelLevel
            }
            4 -> {
                jewel4Type = jewelType
                jewel4Level = jewelLevel
            }
            else -> throw RuntimeException("Jewel slot $slot is not supported")
        }
    }
}
