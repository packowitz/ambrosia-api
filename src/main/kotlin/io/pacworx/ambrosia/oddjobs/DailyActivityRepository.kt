package io.pacworx.ambrosia.oddjobs

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface DailyActivityRepository : JpaRepository<DailyActivity, Long>