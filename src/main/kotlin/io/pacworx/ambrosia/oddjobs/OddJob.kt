package io.pacworx.ambrosia.oddjobs

import com.fasterxml.jackson.annotation.JsonIgnore
import io.pacworx.ambrosia.hero.Rarity
import io.pacworx.ambrosia.loot.LootedItem
import java.time.Instant
import javax.persistence.*

@Entity
data class OddJob(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    val playerId: Long,
    val oddJobBaseId: Long,
    @JsonIgnore val created: Instant = Instant.now(),
    val level: Int,
    @Enumerated(EnumType.STRING) val rarity: Rarity,
    @Enumerated(EnumType.STRING) val jobType: OddJobType,
    val jobAmount: Int,
    var jobAmountDone: Int = 0,
    @JsonIgnore val lootBoxId: Long
) {
    @Transient
    var reward: List<LootedItem> = emptyList()
}