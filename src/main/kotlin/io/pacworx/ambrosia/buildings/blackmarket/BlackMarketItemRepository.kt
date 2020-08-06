package io.pacworx.ambrosia.buildings.blackmarket

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface BlackMarketItemRepository : JpaRepository<BlackMarketItem, Long> {

    fun findAllByOrderBySortOrderAsc(): List<BlackMarketItem>

    fun findAllByActiveIsTrueOrderBySortOrderAsc(): List<BlackMarketItem>
}