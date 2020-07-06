package io.pacworx.ambrosia.battle.offline

import com.fasterxml.jackson.annotation.JsonIgnore
import io.pacworx.ambrosia.fights.Fight
import java.time.Instant
import java.time.temporal.ChronoUnit
import javax.persistence.*

@Entity
data class Mission(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    val playerId: Long,
    @OneToOne
    @JoinColumn(name = "fight_id")
    @JsonIgnore
    val fight: Fight,
    val mapId: Long? = null,
    @Column(name = "pos_x") val posX: Int? = null,
    @Column(name = "pos_y") val posY: Int? = null,
    val vehicleId: Long,
    val slotNumber: Int,
    val hero1Id: Long?,
    val hero2Id: Long?,
    val hero3Id: Long?,
    val hero4Id: Long?,
    val totalCount: Int,
    var wonCount: Int = 0,
    var lostCount: Int = 0,
    @JsonIgnore val startTimestamp: Instant,
    @JsonIgnore val finishTimestamp: Instant
) {
    @OneToMany(cascade = [CascadeType.ALL], orphanRemoval = true)
    @JoinColumn(name = "mission_id")
    @OrderBy("start_timestamp")
    var battles: List<OfflineBattle> = ArrayList()

    @Transient
    var lootCollected: Boolean? = null

    fun isMissionFinished(): Boolean = lootCollected == true || Instant.now().isAfter(finishTimestamp)

    fun getNextUpdateSeconds(): Long = battles.find { it.battleStarted && !it.battleFinished }?.getSecondsUntilDone() ?: 0

    fun getDuration(): Long = startTimestamp.until(finishTimestamp, ChronoUnit.SECONDS)

    fun getSecondsUntilDone(): Long =
        if (isMissionFinished()) { 0 } else { Instant.now().until(finishTimestamp, ChronoUnit.SECONDS) + 2 }

}
