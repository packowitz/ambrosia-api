package io.pacworx.ambrosia.progress

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ProgressRepository: JpaRepository<Progress, Long>
