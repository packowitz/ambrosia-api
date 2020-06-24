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
    val rarity: Rarity,
    val durationMinutes: Int,
    val xp: Int
) {

    @ManyToOne
    @JoinColumn(name = "loot_box_id")
    lateinit var lootBox: LootBox
}