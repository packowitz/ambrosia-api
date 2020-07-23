package io.pacworx.ambrosia.buildings.merchant

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface MerchantItemRepository : JpaRepository<MerchantItem, Long> {

    fun findAllByOrderByMerchantLevelDescSortOrderAsc(): List<MerchantItem>

    fun findAllByMerchantLevelOrderBySortOrder(merchantLevel: Int): List<MerchantItem>
}