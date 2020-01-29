package io.pacworx.ambrosia.gear

import io.pacworx.ambrosia.enums.*
import io.pacworx.ambrosia.properties.PropertyService
import org.springframework.stereotype.Service
import kotlin.random.Random

@Service
class GearService(private val propertyService: PropertyService) {

    fun createGear(playerId: Long,
                   sets: List<GearSet>,
                   rarities: List<Rarity>,
                   gearTypes: List<GearType>,
                   oneSlotChance: Int = 65,
                   twoSlotChance: Int = 40,
                   threeSlotChance: Int = 18,
                   fourSlotChance: Int = 2,
                   specialSlotChnace: Int = 10): Gear {
        val gearSet: GearSet = sets.random()
        val rarity: Rarity = rarities.random()
        val type: GearType = gearTypes.random()
        val stat: HeroStat = propertyService.getPossibleGearStats(type, rarity).random()
        val valueRange = propertyService.getGearValueRange(type, rarity, stat)
        val statQuality = Random.nextInt(0, 101)
        val statValue = valueRange.first + ((statQuality * (valueRange.second - valueRange.first)) / 100)

        val jewelSlot4: GearJewelSlot? = if (procs(fourSlotChance)) { getJewelSlot(type) } else { null }
        val jewelSlot3: GearJewelSlot? = if (jewelSlot4 != null || procs(threeSlotChance)) { getJewelSlot(type) } else { null }
        val jewelSlot2: GearJewelSlot? = if (jewelSlot3 != null || procs(twoSlotChance)) { getJewelSlot(type) } else { null }
        val jewelSlot1: GearJewelSlot? = if (jewelSlot2 != null || procs(oneSlotChance)) { getJewelSlot(type) } else { null }
        val specialJewelSlot = type == GearType.ARMOR && procs(specialSlotChnace)

        return Gear(
            playerId = playerId,
            set = gearSet,
            rarity = rarity,
            type = type,
            stat = stat,
            statValue = statValue,
            statQuality = statQuality,
            jewelSlot1 = jewelSlot1,
            jewelSlot2 = jewelSlot2,
            jewelSlot3 = jewelSlot3,
            jewelSlot4 = jewelSlot4,
            specialJewelSlot = specialJewelSlot
        )
    }

    private fun getJewelSlot(gearType: GearType): GearJewelSlot {
        return GearJewelSlot.values().toList().filter { it != GearJewelSlot.SPECIAL && it.gearTypes.contains(gearType) }.random()
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
}
