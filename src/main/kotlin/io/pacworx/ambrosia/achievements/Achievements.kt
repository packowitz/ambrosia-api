package io.pacworx.ambrosia.achievements

import io.pacworx.ambrosia.buildings.GenomeType
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
    var academyAscGained: Long = 0
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
}