package io.pacworx.ambrosia.achievements

import io.pacworx.ambrosia.loot.LootedItem
import javax.persistence.*

@Entity
class Task(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    val number: Int,
    @Enumerated(EnumType.STRING)
    val taskType: AchievementRewardType,
    val taskAmount: Long,
    val lootBoxId: Long
) {
    @Transient
    var reward: List<LootedItem> = emptyList()
}