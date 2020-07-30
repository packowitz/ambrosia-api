package io.pacworx.ambrosia.achievements

import io.pacworx.ambrosia.loot.LootedItem
import javax.persistence.*

@Entity
data class AchievementReward(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    val starter: Boolean,
    val name: String,
    @Enumerated(EnumType.STRING)
    val achievementType: AchievementRewardType,
    val achievementAmount: Long,
    var followUpReward: Long?,
    val lootBoxId: Long
) {
    @Transient
    var reward: List<LootedItem> = emptyList()
}