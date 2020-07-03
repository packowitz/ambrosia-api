package io.pacworx.ambrosia.oddjobs

import io.pacworx.ambrosia.hero.Rarity
import javax.persistence.*

@Entity
data class OddJobBase(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    val active: Boolean,
    val level: Int,
    @Enumerated(EnumType.STRING)
    val rarity: Rarity,
    @Enumerated(EnumType.STRING)
    val jobType: OddJobType,
    val jobAmount: Int,
    val lootBoxId: Long
)