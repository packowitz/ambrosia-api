package io.pacworx.ambrosia.progress

import io.pacworx.ambrosia.hero.Rarity
import io.pacworx.ambrosia.upgrade.Modification
import javax.persistence.Entity
import javax.persistence.Id

@Entity
data class Progress(
    @Id
    val playerId: Long,
    var xp: Int = 0,
    var maxXp: Int = 100,
    var level: Int = 1,
    var vipPoints: Int = 0,
    var vipLevel: Int = 0,
    var vipMaxPoints: Int = 100,
    var currentMapId: Long? = null,
    var expeditionLevel: Int = 0,
    var expeditionSpeed: Int = 100,
    var numberOddJobs: Int = 3,
    var garageSlots: Int = 1,
    var offlineBattleSpeed: Int = 100,
    var maxOfflineBattlesPerMission: Int = 5,
    var builderQueueLength: Int = 1,
    var builderSpeed: Int = 100,
    var barrackSize: Int = 0,
    var gearQualityIncrease: Int = 0,
    var maxTrainingLevel: Int = 0,
    var trainingXpBoost: Int = 0,
    var trainingAscBoost: Int = 0,
    var vehicleUpgradeLevel: Int = 0,
    var incubators: Int = 0,
    var labSpeed: Int = 100,
    var simpleGenomesNeeded: Int = 0,
    var commonGenomesNeeded: Int = 0,
    var uncommonGenomesNeeded: Int = 0,
    var rareGenomesNeeded: Int = 0,
    var epicGenomesNeeded: Int = 0,
    var simpleIncubationUpPerMil: Int = 0,
    var commonIncubationUpPerMil: Int = 0,
    var uncommonIncubationUpPerMil: Int = 0,
    var rareIncubationUpPerMil: Int = 0,
    var uncommonStartingLevel: Int = 1,
    var maxJewelUpgradingLevel: Int = 0,
    var jewelMergeDoubleChance: Int = 0,
    var gearModificationRarity: Int = 0,
    var gearModificationSpeed: Int = 100,
    var gearBreakDownRarity: Int = 0,
    var gearBreakDownResourcesInc: Int = 0,
    var reRollGearQualityEnabled: Boolean = false,
    var reRollGearStatEnabled: Boolean = false,
    var incGearRarityEnabled: Boolean = false,
    var reRollGearJewelEnabled: Boolean = false,
    var addGearJewelEnabled: Boolean = false,
    var addGearSpecialJewelEnabled: Boolean = false,
    var negotiationLevel: Int = 0,
    var tradingEnabled: Boolean = false,
    var blackMarketEnabled: Boolean = false,
    var carYardEnabled: Boolean = false,
    var merchantLevel: Int = 0
) {

    fun modificationAllowed(gearRarity: Rarity, modification: Modification): Boolean {
        return gearRarity.stars <= gearModificationRarity && when (modification) {
            Modification.REROLL_QUALITY -> reRollGearQualityEnabled
            Modification.REROLL_STAT -> reRollGearStatEnabled
            Modification.INC_RARITY -> incGearRarityEnabled
            Modification.ADD_JEWEL -> addGearJewelEnabled
            Modification.REROLL_JEWEL_1, Modification.REROLL_JEWEL_2, Modification.REROLL_JEWEL_3, Modification.REROLL_JEWEL_4 -> reRollGearJewelEnabled
            Modification.ADD_SPECIAL_JEWEL -> addGearSpecialJewelEnabled
        }
    }
}
