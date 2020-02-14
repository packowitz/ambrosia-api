package io.pacworx.ambrosia.vehicle

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface VehicleBaseRepository: JpaRepository<VehicleBase, Long>
