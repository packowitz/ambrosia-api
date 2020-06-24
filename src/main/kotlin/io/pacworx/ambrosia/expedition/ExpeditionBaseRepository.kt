package io.pacworx.ambrosia.expedition

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ExpeditionBaseRepository: JpaRepository<ExpeditionBase, Long>