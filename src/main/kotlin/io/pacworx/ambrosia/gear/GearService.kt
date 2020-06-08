package io.pacworx.ambrosia.gear

import io.pacworx.ambrosia.common.procs
import io.pacworx.ambrosia.exceptions.EntityNotFoundException
import io.pacworx.ambrosia.hero.HeroStat
import io.pacworx.ambrosia.hero.Rarity
import io.pacworx.ambrosia.player.Player
import io.pacworx.ambrosia.properties.PropertyService
import io.pacworx.ambrosia.upgrade.Modification
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import kotlin.random.Random

@Service
class GearService(private val propertyService: PropertyService,
                  private val gearRepository: GearRepository) {

    fun getGear(player: Player, gearId: Long): Gear {
        return gearRepository.findByIdOrNull(gearId)
            ?: throw EntityNotFoundException(player, "gear", gearId)
    }

    fun modifyGear(player: Player, modification: Modification, gearId: Long): Gear {
        val gear = getGear(player, gearId)
        when (modification) {
            Modification.REROLL_QUALITY -> {
                val valueRange = propertyService.getGearValueRange(gear.type, gear.rarity, gear.stat)
                gear.statQuality = Random.nextInt(0, 101)
                gear.statValue = valueRange.first + ((gear.statQuality * (valueRange.second - valueRange.first)) / 100)
                gear.modificationAllowed = modification
            }
            Modification.REROLL_STAT -> {
                gear.stat = propertyService.getPossibleGearStats(gear.type, gear.rarity).random()
                val valueRange = propertyService.getGearValueRange(gear.type, gear.rarity, gear.stat)
                gear.statValue = valueRange.first + ((gear.statQuality * (valueRange.second - valueRange.first)) / 100)
                gear.modificationAllowed = modification
            }
            Modification.INC_RARITY -> {
                gear.rarity = Rarity.values().find { it.stars == gear.rarity.stars + 1 }!!
                val valueRange = propertyService.getGearValueRange(gear.type, gear.rarity, gear.stat)
                gear.statValue = valueRange.first + ((gear.statQuality * (valueRange.second - valueRange.first)) / 100)
            }
            Modification.ADD_JEWEL -> {
                if (gear.jewelSlot1 == null) {
                    gear.jewelSlot1 = getJewelSlot(gear.type)
                    gear.modificationAllowed = Modification.REROLL_JEWEL_1
                } else if (gear.jewelSlot2 == null) {
                    gear.jewelSlot2 = getJewelSlot(gear.type)
                    gear.modificationAllowed = Modification.REROLL_JEWEL_2
                } else if (gear.jewelSlot3 == null) {
                    gear.jewelSlot3 = getJewelSlot(gear.type)
                    gear.modificationAllowed = Modification.REROLL_JEWEL_3
                } else if (gear.jewelSlot4 == null) {
                    gear.jewelSlot4 = getJewelSlot(gear.type)
                    gear.modificationAllowed = Modification.REROLL_JEWEL_4
                }
            }
            Modification.REROLL_JEWEL_1 -> {
                gear.jewelSlot1 = getJewelSlot(gear.type)
                gear.modificationAllowed = modification
            }
            Modification.REROLL_JEWEL_2 -> {
                gear.jewelSlot2 = getJewelSlot(gear.type)
                gear.modificationAllowed = Modification.REROLL_JEWEL_2
            }
            Modification.REROLL_JEWEL_3 -> {
                gear.jewelSlot3 = getJewelSlot(gear.type)
                gear.modificationAllowed = Modification.REROLL_JEWEL_3
            }
            Modification.REROLL_JEWEL_4 -> {
                gear.jewelSlot4 = getJewelSlot(gear.type)
                gear.modificationAllowed = Modification.REROLL_JEWEL_4
            }
            Modification.ADD_SPECIAL_JEWEL -> {
                gear.specialJewelSlot = true
            }
        }
        gear.modificationInProgress = false
        gear.modificationPerformed = true
        return gear
    }

    fun createGear(playerId: Long,
                   sets: List<GearSet>,
                   gearTypes: List<GearType>,
                   legendaryChance: Int = 5,
                   epicChance: Int = 10,
                   rareChance: Int = 15,
                   uncommonChance: Int = 25,
                   commonChance: Int = 40,
                   oneSlotChance: Int = 65,
                   twoSlotChance: Int = 40,
                   threeSlotChance: Int = 18,
                   fourSlotChance: Int = 2,
                   specialSlotChance: Int = 10): Gear {
        val gearSet: GearSet = sets.random()
        val rarityRandom = Random.nextInt(100)
        val rarity: Rarity = when {
            procs(legendaryChance, rarityRandom) -> Rarity.LEGENDARY
            procs(epicChance, rarityRandom - legendaryChance) -> Rarity.EPIC
            procs(rareChance, rarityRandom - legendaryChance - epicChance) -> Rarity.RARE
            procs(uncommonChance, rarityRandom - legendaryChance - epicChance - rareChance) -> Rarity.UNCOMMON
            procs(commonChance, rarityRandom - legendaryChance - epicChance - rareChance - uncommonChance) -> Rarity.COMMON
            else -> Rarity.SIMPLE
        }
        val type: GearType = gearTypes.random()
        val stat: HeroStat = propertyService.getPossibleGearStats(type, rarity).random()
        val valueRange = propertyService.getGearValueRange(type, rarity, stat)
        val statQuality = Random.nextInt(0, 101)
        val statValue = valueRange.first + ((statQuality * (valueRange.second - valueRange.first)) / 100)

        val slotRandom = Random.nextInt(100)
        val jewelSlot4: GearJewelSlot? = if (procs(fourSlotChance, slotRandom)) { getJewelSlot(type) } else { null }
        val jewelSlot3: GearJewelSlot? = if (jewelSlot4 != null || procs(threeSlotChance, slotRandom - fourSlotChance)) { getJewelSlot(type) } else { null }
        val jewelSlot2: GearJewelSlot? = if (jewelSlot3 != null || procs(twoSlotChance, slotRandom - fourSlotChance - threeSlotChance)) { getJewelSlot(type) } else { null }
        val jewelSlot1: GearJewelSlot? = if (jewelSlot2 != null || procs(oneSlotChance, slotRandom - fourSlotChance - threeSlotChance - twoSlotChance)) { getJewelSlot(type) } else { null }
        val specialJewelSlot = type == GearType.ARMOR && procs(specialSlotChance)

        return gearRepository.save(Gear(
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
        ))
    }

    private fun getJewelSlot(gearType: GearType): GearJewelSlot {
        return GearJewelSlot.values().toList().filter { it != GearJewelSlot.SPECIAL && it.gearTypes.contains(gearType) }.random()
    }
}
