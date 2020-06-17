package io.pacworx.ambrosia.progress

import io.pacworx.ambrosia.upgrade.Modification
import io.pacworx.ambrosia.hero.Rarity
import javax.persistence.Entity
import javax.persistence.Id

@Entity
data class Progress(
    @Id
    val playerId: Long,
    var garageSlots: Int = 1,
    var offlineBattleSpeed: Int = 100,
    var maxOfflineBattlesPerMission: Int = 5,
    var builderQueueLength: Int = 1,
    var builderSpeed: Int = 100,
    var barrackSize: Int = 0,
    var maxTrainingLevel: Int = 0,
    var vehicleUpgradeLevel: Int = 0,
    var incubators: Int = 0,
    var labSpeed: Int = 100,
    var maxJewelUpgradingLevel: Int = 0,
    var gearModificationRarity: Int = 0,
    var gearModificationSpeed: Int = 100,
    var gearBreakDownRarity: Int = 0,
    var gearBreakDownResourcesInc: Int = 0,
    var reRollGearQualityEnabled: Boolean = false,
    var reRollGearStatEnabled: Boolean = false,
    var incGearRarityEnabled: Boolean = false,
    var reRollGearJewelEnabled: Boolean = false,
    var addGearJewelEnabled: Boolean = false,
    var addGearSpecialJewelEnabled: Boolean = false
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
