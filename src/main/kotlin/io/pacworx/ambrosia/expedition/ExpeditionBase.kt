package io.pacworx.ambrosia.expedition

import io.pacworx.ambrosia.hero.Rarity
import io.pacworx.ambrosia.loot.LootBox
import javax.persistence.*
import javax.validation.constraints.Max
import javax.validation.constraints.Min

@Entity
data class ExpeditionBase(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    val name: String,
    val description: String,
    @Min(1)
    @Max(6)
    val level: Int,
    @Enumerated(EnumType.STRING) val rarity: Rarity,
    val durationHours: Int,
    val xp: Int,
    val lootBoxId: Long
)