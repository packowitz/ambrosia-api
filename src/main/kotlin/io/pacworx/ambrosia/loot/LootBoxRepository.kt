package io.pacworx.ambrosia.loot

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface LootBoxRepository: JpaRepository<LootBox, Long>
