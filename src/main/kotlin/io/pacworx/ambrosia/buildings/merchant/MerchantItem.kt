package io.pacworx.ambrosia.buildings.merchant

import io.pacworx.ambrosia.resources.ResourceType
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@Entity
data class MerchantItem(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    val merchantLevel: Int,
    val sortOrder: Int,
    val lootBoxId: Long,
    val priceType: ResourceType,
    val priceAmount: Int
)