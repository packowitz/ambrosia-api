package io.pacworx.ambrosia.properties

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface PropertyVersionRepository : JpaRepository<PropertyVersion, Long> {

    fun findAllByActiveIsTrue(): List<PropertyVersion>

    fun findByPropertyTypeAndActiveIsTrue(type: PropertyType): PropertyVersion?
}