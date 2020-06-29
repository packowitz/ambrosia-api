package io.pacworx.ambrosia.expedition

import com.fasterxml.jackson.annotation.JsonIgnore
import java.time.Instant
import javax.persistence.*

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
)