package io.pacworx.ambrosia.achievements

import com.fasterxml.jackson.annotation.JsonInclude
import io.pacworx.ambrosia.loot.LootedItem
import javax.persistence.*

@Entity
@JsonInclude(JsonInclude.Include.NON_NULL)
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