package io.pacworx.ambrosia.achievements

import io.pacworx.ambrosia.buildings.GenomeType
import io.pacworx.ambrosia.loot.LootBoxResult
import io.pacworx.ambrosia.resources.ResourceType
import javax.persistence.Entity
import javax.persistence.Id

@Entity
data class Achievements(
    @Id
    val playerId: Long,
    var simpleIncubationsDone: Int = 0,
    var commonIncubationsDone: Int = 0,
    var uncommonIncubationsDone: Int = 0,
    var rareIncubationsDone: Int = 0,
    var epicIncubationsDone: Int = 0,
    var expeditionsDone: Int = 0,
    var oddJobsDone: Int = 0,
    var dailyRewardsClaimed: Int = 0,
    var academyXpGained: Long = 0,
    var academyAscGained: Long = 0,
    var steamUsed: Long = 0,
    var cogwheelsUsed: Long = 0,
    var tokensUsed: Long = 0,
    var coinsUsed: Long = 0,
    var rubiesUsed: Long = 0,
    var metalUsed: Long = 0,
    var ironUsed: Long = 0,
    var steelUsed: Long = 0,
    var woodUsed: Long = 0,
    var brownCoalUsed: Long = 0,
    var blackCoalUsed: Long = 0,
    var merchantItemsBought: Int = 0,
    var mapTilesDiscovered: Int = 0,
    var gearModified: Int = 0,
    var jewelsMerged: Int = 0,
    var buildingsUpgradesDone: Int = 0,
    var vehiclesUpgradesDone: Int = 0,
    var vehiclePartUpgradesDone: Int = 0,
    var buildingMinLevel: Int = 0,
    var woodenKeysCollected: Long = 0,
    var bronzeKeysCollected: Long = 0,
    var silverKeysCollected: Long = 0,
    var goldenKeysCollected: Long = 0,
    var chestsOpened: Long = 0

// TODO HEROES_FULLY_ASCENDED, HERO_LEVEL_REACHED, HEROES_EVOLVED
) {

    fun incubationDone(genomeType: GenomeType) {
        when(genomeType) {
            GenomeType.SIMPLE_GENOME -> simpleIncubationsDone ++
            GenomeType.COMMON_GENOME -> commonIncubationsDone ++
            GenomeType.UNCOMMON_GENOME -> uncommonIncubationsDone ++
            GenomeType.RARE_GENOME -> rareIncubationsDone ++
            GenomeType.EPIC_GENOME -> epicIncubationsDone ++
        }
    }

    fun resourceSpend(resourceType: ResourceType, amount: Int) {
        when(resourceType) {
            ResourceType.STEAM -> steamUsed += amount
            ResourceType.COGWHEELS -> cogwheelsUsed += amount
            ResourceType.TOKENS -> tokensUsed += amount
            ResourceType.COINS -> coinsUsed += amount
            ResourceType.RUBIES -> rubiesUsed += amount
            ResourceType.METAL -> metalUsed += amount
            ResourceType.IRON -> ironUsed += amount
            ResourceType.STEEL -> steelUsed += amount
            ResourceType.WOOD -> woodUsed += amount
            ResourceType.BROWN_COAL -> brownCoalUsed += amount
            ResourceType.BLACK_COAL -> blackCoalUsed += amount
            else -> {}
        }
    }

    fun lootBoxOpened(lootBoxResult: LootBoxResult) {
        woodenKeysCollected += lootBoxResult.items.filter { it.resource?.type == ResourceType.WOODEN_KEYS }.sumBy { it.resource?.amount ?: 0 }
        bronzeKeysCollected += lootBoxResult.items.filter { it.resource?.type == ResourceType.BRONZE_KEYS }.sumBy { it.resource?.amount ?: 0 }
        silverKeysCollected += lootBoxResult.items.filter { it.resource?.type == ResourceType.SILVER_KEYS }.sumBy { it.resource?.amount ?: 0 }
        goldenKeysCollected += lootBoxResult.items.filter { it.resource?.type == ResourceType.GOLDEN_KEYS }.sumBy { it.resource?.amount ?: 0 }
    }
}