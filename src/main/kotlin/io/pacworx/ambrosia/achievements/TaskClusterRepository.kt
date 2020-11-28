package io.pacworx.ambrosia.achievements

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface TaskClusterRepository : JpaRepository<TaskCluster, Long>