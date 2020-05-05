package io.pacworx.ambrosia.gear

import com.fasterxml.jackson.annotation.JsonFormat
import io.pacworx.ambrosia.upgrade.Modification
import io.pacworx.ambrosia.hero.HeroStat
import io.pacworx.ambrosia.hero.Rarity
import java.lang.RuntimeException
import javax.persistence.*

@Entity
data class Gear(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    val playerId: Long,
    var equippedTo: Long? = null,
    var modificationInProgress: Boolean = false,
    var modificationPerformed: Boolean = false,
    @Enumerated(EnumType.STRING)
    var modificationAllowed: Modification? = null,
    @Enumerated(EnumType.STRING)
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    val set: GearSet,
    @Enumerated(EnumType.STRING)
    var rarity: Rarity,
    @Enumerated(EnumType.STRING)
    val type: GearType,
    @Enumerated(EnumType.STRING)
    var stat: HeroStat,
    var statValue: Int,
    var statQuality: Int,
    @Enumerated(EnumType.STRING)
    var jewelSlot1: GearJewelSlot? = null,
    @Enumerated(EnumType.STRING)
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    var jewel1Type: JewelType? = null,
    var jewel1Level: Int? = null,
    @Enumerated(EnumType.STRING)
    var jewelSlot2: GearJewelSlot? = null,
    @Enumerated(EnumType.STRING)
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    var jewel2Type: JewelType? = null,
    var jewel2Level: Int? = null,
    @Enumerated(EnumType.STRING)
    var jewelSlot3: GearJewelSlot? = null,
    @Enumerated(EnumType.STRING)
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    var jewel3Type: JewelType? = null,
    var jewel3Level: Int? = null,
    @Enumerated(EnumType.STRING)
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    var jewelSlot4: GearJewelSlot? = null,
    @Enumerated(EnumType.STRING)
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    var jewel4Type: JewelType? = null,
    var jewel4Level: Int? = null,
    var specialJewelSlot: Boolean = false,
    @Enumerated(EnumType.STRING)
    var specialJewelType: JewelType? = null,
    var specialJewelLevel: Int? = null
) {

    fun getGearQuality(): GearQuality {
        return when {
            statQuality > 100 -> GearQuality.GODLIKE
            statQuality == 100 -> GearQuality.PERFECT
            statQuality >= 95 -> GearQuality.FLAWLESS
            statQuality >= 90 -> GearQuality.AWESOME
            statQuality >= 80 -> GearQuality.GOOD
            statQuality >= 70 -> GearQuality.USEFUL
            statQuality >= 50 -> GearQuality.ORDINARY
            statQuality >= 25 -> GearQuality.RUSTY
            else -> GearQuality.SHABBY
        }
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

    fun unplugJewel(slot: Int) {
        when (slot) {
            0 -> {
                specialJewelType = null
                specialJewelLevel = null
            }
            1 -> {
                jewel1Type = null
                jewel1Level = null
            }
            2 -> {
                jewel2Type = null
                jewel2Level = null
            }
            3 -> {
                jewel3Type = null
                jewel3Level = null
            }
            4 -> {
                jewel4Type = null
                jewel4Level = null
            }
            else -> throw RuntimeException("Jewel slot $slot is not supported")
        }
    }

    fun isModificationAllowed(modification: Modification): Boolean {
        if (equippedTo != null || modificationInProgress) {
            return false
        }
        if (modificationPerformed) {
            return modification == modificationAllowed
        }
        return when (modification) {
            Modification.INC_RARITY -> rarity != Rarity.LEGENDARY
            Modification.ADD_JEWEL -> jewelSlot4 == null
            Modification.REROLL_JEWEL_1 -> jewelSlot1 != null && jewel1Type == null
            Modification.REROLL_JEWEL_2 -> jewelSlot2 != null && jewel2Type == null
            Modification.REROLL_JEWEL_3 -> jewelSlot3 != null && jewel3Type == null
            Modification.REROLL_JEWEL_4 -> jewelSlot4 != null && jewel4Type == null
            Modification.ADD_SPECIAL_JEWEL -> type == GearType.ARMOR && !specialJewelSlot
            else -> true
        }
    }
}
