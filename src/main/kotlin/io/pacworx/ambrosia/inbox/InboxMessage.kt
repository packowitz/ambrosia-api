package io.pacworx.ambrosia.inbox

import com.fasterxml.jackson.annotation.JsonIgnore
import java.time.Instant
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import javax.persistence.*
import kotlin.math.max

@Entity
data class InboxMessage(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    val playerId: Long,
    @Enumerated(EnumType.STRING)
    val messageType: InboxMessageType,
    val senderId: Long? = null,
    val read: Boolean = false,
    val serviceAnswerNeeded: Boolean? = null,
    @JsonIgnore val sendTimestamp: LocalDateTime = LocalDateTime.now(),
    @JsonIgnore val validTimestamp: LocalDateTime,
    val message: String,
    @OneToMany(cascade = [CascadeType.ALL], orphanRemoval = true)
    @JoinColumn(name = "message_id")
    @OrderBy("number ASC")
    val items: List<InboxMessageItem> = ArrayList()
) {


    fun getAgeInSeconds(): Long {
        return max(sendTimestamp.until(LocalDateTime.now(), ChronoUnit.SECONDS), 0)
    }

    fun getValidInSeconds(): Long {
        return max(Instant.now().until(validTimestamp, ChronoUnit.SECONDS), 0)
    }
}