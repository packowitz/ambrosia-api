package io.pacworx.ambrosia.buildings

import javax.persistence.Entity
import javax.persistence.Id

@Entity
data class AutoBreakdownConfiguration(
    @Id
    var playerId: Long,
    val simpleMinJewelSlots: Int = 0,
    val simpleMinQuality: Int = 0,
    val commonMinJewelSlots: Int = 0,
    val commonMinQuality: Int = 0,
    val uncommonMinJewelSlots: Int = 0,
    val uncommonMinQuality: Int = 0,
    val rareMinJewelSlots: Int = 0,
    val rareMinQuality: Int = 0,
    val epicMinJewelSlots: Int = 0,
    val epicMinQuality: Int = 0,
    val legendaryMinJewelSlots: Int = 0,
    val legendaryMinQuality: Int = 0
)