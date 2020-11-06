package io.pacworx.ambrosia.expedition

import com.fasterxml.jackson.annotation.JsonIgnore
import java.time.Instant
import java.time.temporal.ChronoUnit
import javax.persistence.*
import kotlin.math.max

@Entity
data class Expedition(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    @ManyToOne
    @JoinColumn(name = "expedition_base_id")
    val expeditionBase: ExpeditionBase,
    @JsonIgnore var active: Boolean,
    @JsonIgnore val created: Instant,
    @JsonIgnore val availableUntil: Instant
) {
    fun getSecondsUntilDisappear(): Long {
        return max(Instant.now().until(availableUntil, ChronoUnit.SECONDS) + 2, 0)
    }
}