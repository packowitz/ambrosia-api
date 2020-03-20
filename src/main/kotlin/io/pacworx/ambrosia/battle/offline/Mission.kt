package io.pacworx.ambrosia.battle.offline

import com.fasterxml.jackson.annotation.JsonIgnore
import io.pacworx.ambrosia.fights.Fight
import java.time.Instant
import java.time.temporal.ChronoUnit
import javax.persistence.CascadeType
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.OneToMany
import javax.persistence.OneToOne
import javax.persistence.OrderBy

@Entity
data class Mission(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    val playerId: Long,
    @OneToOne(cascade = [CascadeType.ALL])
    @JoinColumn(name = "fightId")
    val fight: Fight,
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

    fun isMissionFinished(): Boolean = Instant.now().isAfter(finishTimestamp)

    fun nextUpdateSeconds(): Long = battles.find { it.battleStarted && !it.battleFinished }?.secondsUntilDone() ?: 0

    fun getDuration(): Long = startTimestamp.until(finishTimestamp, ChronoUnit.SECONDS)

    fun secondsUntilDone(): Long =
        if (isMissionFinished()) { 0 } else { Instant.now().until(finishTimestamp, ChronoUnit.SECONDS) }

}
