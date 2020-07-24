package io.pacworx.ambrosia.buildings.merchant

import com.fasterxml.jackson.annotation.JsonFormat
import io.pacworx.ambrosia.resources.ResourceType
import javax.persistence.*

@Entity
data class MerchantItem(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    val merchantLevel: Int,
    val sortOrder: Int,
    val lootBoxId: Long,
    @Enumerated(EnumType.STRING)
    @field:JsonFormat(shape = JsonFormat.Shape.STRING)
    val priceType: ResourceType,
    val priceAmount: Int
)