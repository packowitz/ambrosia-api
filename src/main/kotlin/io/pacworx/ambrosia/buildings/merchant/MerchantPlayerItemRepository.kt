package io.pacworx.ambrosia.buildings.merchant

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface MerchantPlayerItemRepository : JpaRepository<MerchantPlayerItem, Long> {

    fun findAllByPlayerIdOrderBySortOrder(playerId: Long): List<MerchantPlayerItem>
}