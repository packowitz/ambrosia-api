package io.pacworx.ambrosia.battle.offline

import com.fasterxml.jackson.annotation.JsonIgnore
import io.pacworx.ambrosia.loot.Looted
import java.time.Instant
import java.time.temporal.ChronoUnit
import javax.persistence.Entity
import javax.persistence.Id

@Entity
data class OfflineBattle(
    @Id
    val battleId: Long,
    @JsonIgnore val startTimestamp: Instant,
    @JsonIgnore val finishTimestamp: Instant,
    var battleFinished: Boolean = false,
    var battleStarted: Boolean = false,
    @JsonIgnore val battleWon: Boolean
) {

    @Transient
    var looted: List<Looted>? = null
    @Transient
    var cancelled: Boolean? = null

    fun getDuration(): Long = startTimestamp.until(finishTimestamp, ChronoUnit.SECONDS)

    fun getSecondsUntilDone(): Long {
        return if (battleFinished || !battleStarted) { 0 } else { Instant.now().until(finishTimestamp, ChronoUnit.SECONDS) }
    }

    fun isBattleSuccess(): Boolean? {
        return if (battleFinished) {
            battleWon
        } else {
            null
        }
    }
}
