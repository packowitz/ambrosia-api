package io.pacworx.ambrosia.expedition

import com.fasterxml.jackson.annotation.JsonIgnore
import io.pacworx.ambrosia.hero.Rarity
import io.pacworx.ambrosia.loot.LootedItem
import io.pacworx.ambrosia.speedup.Speedup
import java.time.Instant
import java.time.temporal.ChronoUnit
import javax.persistence.*
import javax.validation.constraints.Max
import javax.validation.constraints.Min
import kotlin.jvm.Transient
import kotlin.math.max

@Entity
data class PlayerExpedition(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    val playerId: Long,
    val expeditionId: Long,
    val vehicleId: Long,
    val hero1Id: Long?,
    val hero2Id: Long?,
    val hero3Id: Long?,
    val hero4Id: Long?,
    var completed: Boolean,
    val name: String,
    val description: String,
    val level: Int,
    @Enumerated(EnumType.STRING) val rarity: Rarity,
    @JsonIgnore val startTimestamp: Instant,
    @JsonIgnore val finishTimestamp: Instant
) {
    @Transient
    var speedup: Speedup? = null

    @Transient
    var lootedItems: List<LootedItem>? = null

    fun getDuration(): Long = startTimestamp.until(finishTimestamp, ChronoUnit.SECONDS)

    fun getSecondsUntilDone(): Long {
        return max(Instant.now().until(finishTimestamp, ChronoUnit.SECONDS) + 2, 0)
    }
}