package io.pacworx.ambrosia.io.pacworx.ambrosia.services

import io.pacworx.ambrosia.io.pacworx.ambrosia.enums.*
import io.pacworx.ambrosia.io.pacworx.ambrosia.models.Gear
import org.springframework.stereotype.Service
import kotlin.random.Random

@Service
class GearService {

    fun createGear(playerId: Long,
                   sets: List<GearSet>,
                   rarities: List<Rarity>,
                   gearTypes: List<GearType>,
                   zeroSlotsChance: Double = 0.05,
                   oneSlotChance: Double = 0.35,
                   twoSlotChance: Double = 0.4,
                   threeSlotChance: Double = 0.18,
                   fourSlotChance: Double = 0.02): Gear {
        val gearSet: GearSet = sets.random()
        val rarity: Rarity = rarities.random()
        val type: GearType = gearTypes.random()
        val stat: GearStat = type.stats.random()
        val valueRange = stat.range.getValue(rarity)

        var jewelSlot1: GearJewelSlot? = null
        var jewelSlot2: GearJewelSlot? = null
        var jewelSlot3: GearJewelSlot? = null
        var jewelSlot4: GearJewelSlot? = null
        var jewelRdm = Random.nextDouble()
        if (jewelRdm > zeroSlotsChance) {
            jewelRdm -= zeroSlotsChance
            if (jewelRdm > oneSlotChance) {
                jewelSlot1 = getJewelSlot(type)
                jewelRdm -= oneSlotChance
                if (jewelRdm > twoSlotChance) {
                    jewelSlot2 = getJewelSlot(type)
                    jewelRdm -= twoSlotChance
                    if (jewelRdm > threeSlotChance) {
                        jewelSlot3 = getJewelSlot(type)
                        jewelRdm -= threeSlotChance
                        if (jewelRdm > fourSlotChance) {
                            jewelSlot4 = getJewelSlot(type)
                        }
                    }
                }
            }
        }

        var specialJewelType: JewelType? = null
        if (Random.nextDouble() <= 0.1) {
            specialJewelType = JewelType.values().toList()
                .filter { it.slot == GearJewelSlot.SPECIAL && it.gearSet == gearSet }.random()
        }

        return Gear(
            playerId = playerId,
            set = gearSet,
            rarity = rarity,
            type = type,
            stat = stat,
            statValue = Random.nextInt(valueRange.first, valueRange.second + 1),
            jewelSlot1 = jewelSlot1,
            jewelSlot2 = jewelSlot2,
            jewelSlot3 = jewelSlot3,
            jewelSlot4 = jewelSlot4,
            specialJewelSlot = specialJewelType != null,
            specialJewelType = specialJewelType
        )
    }

    private fun getJewelSlot(gearType: GearType): GearJewelSlot {
        return GearJewelSlot.values().toList().filter { it != GearJewelSlot.SPECIAL && it.gearTypes.contains(gearType) }.random()
    }
}